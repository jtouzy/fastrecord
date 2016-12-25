package com.jtouzy.fastrecord.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.fastrecord.annotations.Column;
import com.jtouzy.fastrecord.annotations.Columns;
import com.jtouzy.fastrecord.annotations.Entity;
import com.jtouzy.fastrecord.annotations.Id;
import com.jtouzy.fastrecord.config.ConfigurationBased;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.types.TypeManager;
import com.jtouzy.fastrecord.entity.types.TypeManagerPool;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Singleton EntityLoader Bean
 * This bean is used to load all the EntityDescriptors from the Entity classes.
 *
 * @author jtouzy
 */
@Service
public class EntityLoader extends ConfigurationBased {
    private static final Logger logger = LoggerFactory.getLogger(EntityLoader.class);

    private final LinkedHashMap<Class,EntityDescriptor> entityDescriptorsByClass;
    private final Multimap<Class,ColumnDescriptor> laterLoading = ArrayListMultimap.create();

    @Autowired
    private TypeManagerPool typeManagerPool;

    @Autowired
    public EntityLoader(FastRecordConfiguration configuration) {
        super(configuration);
        entityDescriptorsByClass = new LinkedHashMap<>();
    }

    public LinkedHashMap<Class,EntityDescriptor> load() {
        logger.debug("EntityLoader starts loading entities...");
        this.readEntities();
        if (logger.isDebugEnabled()) {
            this.printAllEntities();
        }
        return entityDescriptorsByClass;
    }

    private void readEntities() {
        Reflections reflections = new Reflections(getConfiguration().getEntitiesClassPackage());
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
        for (Class entityClass : entityClasses) {
            readEntityClass(entityClass);
        }
        loadLateEntities();
    }

    private void readEntityClass(Class<?> entityClass) {
        String tableName = analyzeTableName(entityClass);
        EntityDescriptor descriptor = new EntityDescriptor(entityClass, tableName);
        entityDescriptorsByClass.put(entityClass, descriptor);
        readEntityFields(descriptor);
    }

    private String analyzeTableName(Class<?> entityClass) {
        Entity annotation = entityClass.getAnnotation(Entity.class);
        String tableName = annotation.name();
        if (tableName.isEmpty()) {
            tableName = getConfiguration().getTableNamingStrategy().toDatabaseFormat(entityClass.getSimpleName());
        }
        return tableName;
    }

    private void readEntityFields(EntityDescriptor descriptor) {
        try {
            BeanInfo info = Introspector.getBeanInfo(descriptor.getClazz());
            Field field;
            Method getter, setter;
            ColumnDescriptor columnDescriptor;
            Optional<TypeManager> typeManagerOptional;
            TypeManager typeManager;
            for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals("class")) {
                    continue;
                }
                getter = propertyDescriptor.getReadMethod();
                setter = propertyDescriptor.getWriteMethod();
                field = descriptor.getClazz().getDeclaredField(propertyDescriptor.getName());
                if (setter != null && getter != null) {
                    typeManager = null;
                    typeManagerOptional = typeManagerPool.getTypeManager(field.getType());
                    if (typeManagerOptional.isPresent()) {
                        typeManager = typeManagerOptional.get();
                    }
                    columnDescriptor = new ColumnDescriptor(field, typeManager, getter, setter,
                            analyzeColumnName(field), analyzeId(field));
                    descriptor.addColumnDescriptor(columnDescriptor);
                    if (typeManager == null) {
                        laterLoading.put(descriptor.getClazz(), columnDescriptor);
                    }
                }
            }
        } catch (IntrospectionException | NoSuchFieldException ex) {
            throw new EntityIntrospectionException(ex);
        }
    }

    private String analyzeColumnName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        String columnName = "";
        if (annotation != null) {
            columnName = annotation.name();
        }
        if (columnName.isEmpty()) {
            columnName = getConfiguration().getColumnNamingStrategy().toDatabaseFormat(field.getName());
        }
        return columnName;
    }

    private boolean analyzeId(Field field) {
        return field.getAnnotation(Id.class) != null;
    }

    private void loadLateEntities() {
        logger.debug("Late loading entities...");
        logger.debug("Sorting entities...");
        List<Class> unloadedClasses = sortUnloadedClasses();
        logger.debug("Start loading late entities...");
        EntityDescriptor entityDescriptor, relatedEntityDescriptor;
        List<ColumnDescriptor> idColumns;
        ColumnDescriptor idColumn;
        Collection<ColumnDescriptor> unloadedColumns;
        for (Class unloadedClass : unloadedClasses) {
            unloadedColumns = laterLoading.get(unloadedClass);
            entityDescriptor = entityDescriptorsByClass.get(unloadedClass);
            for (ColumnDescriptor columnDescriptor : unloadedColumns) {
                relatedEntityDescriptor = entityDescriptorsByClass.get(columnDescriptor.getPropertyType());
                if (relatedEntityDescriptor == null) {
                    logger.error("EntityDescriptor for class " + columnDescriptor.getPropertyType() + " not found");
                    throw new UnsupportedJavaTypeException(columnDescriptor.getPropertyType());
                }
                idColumns = relatedEntityDescriptor.getIdColumnDescriptors();
                if (idColumns.size() == 0) {
                    throw new EntityDefinitionException("Related entity [" + columnDescriptor.getPropertyType() +
                            "] defined in [" + unloadedClass + "] doesn't have an Id column");
                }
                if (idColumns.size() > 1) {
                    createMultipleIdEntity(unloadedClass, entityDescriptor, columnDescriptor, idColumns);
                } else {
                    idColumn = idColumns.get(0);
                    logger.debug("Related entity column [{}.{}] registers SQL type [{}] from [{}] in [{}]",
                            unloadedClass, columnDescriptor.getColumnName(), idColumn.getColumnType(), idColumn.getColumnName(),
                            relatedEntityDescriptor.getClazz());
                    columnDescriptor.setTypeManager(idColumn.getTypeManager());
                    columnDescriptor.setRelatedColumn(idColumn);
                }
            }
        }
    }

    private List<Class> sortUnloadedClasses() {
        // Load all class dependencies in a Map
        Map<Class,Set<Class>> classDependencies = new HashMap<>();
        Class insideClass;
        Set<Class> dependencies;
        for (Class unloadedClass : laterLoading.keySet()) {
            classDependencies.putIfAbsent(unloadedClass, new HashSet<>());
            dependencies = classDependencies.get(unloadedClass);
            for (ColumnDescriptor columnDescriptor : laterLoading.get(unloadedClass)) {
                insideClass = columnDescriptor.getPropertyType();
                classDependencies.putIfAbsent(insideClass, new HashSet<>());
                dependencies.add(insideClass);
            }
        }
        // Iterate over those dependencies to sort priority
        List<Class> unloadedClasses = new ArrayList<>();
        List<Class> emptyDependenciesClasses;
        while (!classDependencies.isEmpty()) {
            // Get all the empty dependencies in the full list
            emptyDependenciesClasses = classDependencies.keySet().stream()
                    .filter(k -> classDependencies.get(k).isEmpty()).collect(Collectors.toList());
            // If no dependencies are empty, this may trigger an infinite loop, so we break the loop
            // and add all the last dependencies (in case for example of referencing the entity in his self)
            if (emptyDependenciesClasses.size() == 0) {
                unloadedClasses.addAll(classDependencies.keySet());
                classDependencies.clear();
            } else {
                // Add all this classes to the final list
                unloadedClasses.addAll(emptyDependenciesClasses);
                // Iterate over all this classes
                for (Class emptyDependenciesClass : emptyDependenciesClasses) {
                    // Remove the class from the full list
                    classDependencies.remove(emptyDependenciesClass);
                    // Remove all of the reference of this class in all dependencies
                    for (Class dependantClass : classDependencies.keySet()) {
                        dependencies = classDependencies.get(dependantClass);
                        if (dependencies.contains(emptyDependenciesClass)) {
                            dependencies.remove(emptyDependenciesClass);
                        }
                    }
                }
            }
        }
        return unloadedClasses;
    }

    private void createMultipleIdEntity(Class unloadedClass, EntityDescriptor entityDescriptor,
                                        ColumnDescriptor columnDescriptor,
                                        List<ColumnDescriptor> relatedEntityIdColumns) {
        Columns columnsAnnotation = columnDescriptor.getPropertyField().getAnnotation(Columns.class);
        if (columnsAnnotation == null) {
            throw new EntityDefinitionException("Only one column defined in [" + unloadedClass +
                    "] with type [" + columnDescriptor.getPropertyType() + "].\n" +
                    "The related entity describes more than one Id column : Annotation @Columns is needed.");
        }
        if (columnsAnnotation.columns().length != columnsAnnotation.related().length) {
            throw new EntityDefinitionException("@Columns annotation malformed in [" + unloadedClass +
                    "] : " + columnsAnnotation.columns().length + " columns, " + columnsAnnotation.related().length +
                    " related columns. It must be the same number of columns.");
        }
        if (columnsAnnotation.columns().length != relatedEntityIdColumns.size()) {
            throw new EntityDefinitionException("@Columns annotation malformed in [" + unloadedClass +
                    "] : " + columnsAnnotation.columns().length + " columns, but the related entity have " +
                    relatedEntityIdColumns.size() + " Id columns");
        }
        List<String> relatedColumns = Arrays.asList(columnsAnnotation.related());
        ColumnDescriptor newColumnDescriptor;
        String associatedColumn;
        boolean removeColumnDescriptorOrigin = true;
        for (ColumnDescriptor relatedEntityIdColumn : relatedEntityIdColumns) {
            if (!relatedColumns.contains(relatedEntityIdColumn.getColumnName())) {
                throw new EntityDefinitionException("@Columns annotation malformed in [" + unloadedClass +
                        "] : Id column [" + relatedEntityIdColumn.getPropertyName() +
                        "] is not referenced in the related() annotation property");
            }
            associatedColumn = columnsAnnotation.columns()
                    [relatedColumns.indexOf(relatedEntityIdColumn.getColumnName())];
            if (associatedColumn.isEmpty()) {
                throw new EntityDefinitionException("@Columns annotation malformed in [" + unloadedClass
                        + "] : Entity column cannot be empty.");
            }
            if (associatedColumn.equals(columnDescriptor.getColumnName())) {
                removeColumnDescriptorOrigin = false;
            }
            newColumnDescriptor = new ColumnDescriptor(columnDescriptor.getPropertyField(),
                    relatedEntityIdColumn.getTypeManager(),
                    columnDescriptor.getPropertyGetter(), columnDescriptor.getPropertySetter(),
                    associatedColumn, columnDescriptor.isId());
            newColumnDescriptor.setRelatedColumn(relatedEntityIdColumn);
            entityDescriptor.addColumnDescriptor(newColumnDescriptor);
        }
        if (removeColumnDescriptorOrigin) {
            entityDescriptor.removeColumnDescriptor(columnDescriptor);
        }
    }

    private void printAllEntities() {
        for (EntityDescriptor descriptor : entityDescriptorsByClass.values()) {
            System.out.println(" > " + descriptor);
            for (ColumnDescriptor columnDescriptor : descriptor.getColumnDescriptors()) {
                System.out.println("   > " + columnDescriptor);
            }
        }
    }
}
