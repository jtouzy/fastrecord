package com.jtouzy.fastrecord.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.fastrecord.annotations.Column;
import com.jtouzy.fastrecord.annotations.Entity;
import com.jtouzy.fastrecord.annotations.Id;
import com.jtouzy.fastrecord.config.Configuration;
import com.jtouzy.fastrecord.config.ConfigurationBased;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

class EntityLoader extends ConfigurationBased {
    private static final Logger logger = LoggerFactory.getLogger(EntityLoader.class);

    private final LinkedHashMap<Class,EntityDescriptor> entityDescriptorsByClass;
    private final Multimap<Class,ColumnDescriptor> laterLoading = ArrayListMultimap.create();

    public EntityLoader(Configuration configuration) {
        super(configuration);
        entityDescriptorsByClass = new LinkedHashMap<>();
    }

    public LinkedHashMap<Class,EntityDescriptor> load() {
        logger.debug("EntityLoader starts loading entities...");
        this.readEntities();
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
            int columnType;
            ColumnDescriptor columnDescriptor;
            boolean isId;
            for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals("class")) {
                    continue;
                }
                getter = propertyDescriptor.getReadMethod();
                setter = propertyDescriptor.getWriteMethod();
                field = descriptor.getClazz().getDeclaredField(propertyDescriptor.getName());
                if (setter != null && getter != null) {
                    columnType = analyzeBasicColumnType(field);
                    columnDescriptor = new ColumnDescriptor(field.getName(), field.getType(),
                            getter, setter, analyzeColumnName(field), columnType, analyzeId(field));
                    descriptor.addColumnDescriptor(columnDescriptor);
                    if (columnType == -1) {
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

    private int analyzeBasicColumnType(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        int type = -1;
        if (annotation != null) {
            type = annotation.type();
        }
        if (type == -1) {
            type = getSqlTypeWithJavaType(field.getType());
            /*if (type == -1) {

            }*/
        }
        return type;
    }

    private boolean analyzeId(Field field) {
        return field.getAnnotation(Id.class) != null;
    }

    private int getSqlTypeWithJavaType(Class javaType) {
        if (javaType.equals(String.class)) {
            return Types.VARCHAR;
        } else if (javaType.equals(Integer.class)) {
            return Types.INTEGER;
        }
        return -1;
    }

    private void loadLateEntities() {
        logger.debug("Late loading entities...");
        EntityDescriptor entityDescriptor;
        List<ColumnDescriptor> idColumns;
        ColumnDescriptor idColumn;
        for (Class unloadedClass : laterLoading.keySet()) {
            Collection<ColumnDescriptor> unloadedColumns = laterLoading.get(unloadedClass);
            for (ColumnDescriptor columnDescriptor : unloadedColumns) {
                entityDescriptor = entityDescriptorsByClass.get(columnDescriptor.getPropertyType());
                if (entityDescriptor == null) {
                    logger.error("EntityDescriptor for class " + columnDescriptor.getPropertyType() + " not found");
                    throw new UnsupportedJavaTypeException(columnDescriptor.getPropertyType());
                }
                idColumns = entityDescriptor.getIdColumnDescriptors();
                if (idColumns.size() == 0) {
                    throw new EntityDefinitionException("Related entity [" + columnDescriptor.getPropertyType() +
                            "] defined in [" + unloadedClass + "] doesn't have an Id column");
                }
                if (idColumns.size() > 1) {
                    throw new EntityDefinitionException("Only one column defined in [" + unloadedClass +
                            "] with type [" + columnDescriptor.getPropertyType() + "].\n" +
                            "The related entity describes more than one Id column");
                } else {
                    idColumn = idColumns.get(0);
                    logger.debug("Related entity column [{}.{}] registers SQL type [{}] from [{}] in [{}]",
                            unloadedClass, columnDescriptor.getColumnName(), idColumn.getColumnType(), idColumn.getColumnName(),
                            entityDescriptor.getClazz());
                    columnDescriptor.setColumnType(idColumn.getColumnType());
                }
            }
        }
    }
}
