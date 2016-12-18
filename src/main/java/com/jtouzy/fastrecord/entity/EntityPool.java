package com.jtouzy.fastrecord.entity;

import com.jtouzy.fastrecord.annotations.Column;
import com.jtouzy.fastrecord.annotations.Entity;
import com.jtouzy.fastrecord.config.Configuration;
import org.reflections.Reflections;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityPool {
    private final Configuration configuration;
    private final LinkedHashMap<Class,EntityDescriptor> entityDescriptorsByClass;

    public EntityPool(Configuration configuration) {
        this.configuration = configuration;
        this.entityDescriptorsByClass = new LinkedHashMap<>();
    }

    public void init() {
        readEntities();
    }

    public Set<EntityDescriptor> getEntityDescriptors() {
        return new LinkedHashSet<>(entityDescriptorsByClass.values());
    }

    private void readEntities() {
        Reflections reflections = new Reflections(configuration.getEntitiesClassPath());
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
        for (Class entityClass : entityClasses) {
            readEntityClass(entityClass);
        }
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
            tableName = configuration.getTableNamingStrategy().toDatabaseFormat(entityClass.getSimpleName());
        }
        return tableName;
    }

    private void readEntityFields(EntityDescriptor descriptor) {
        try {
            BeanInfo info = Introspector.getBeanInfo(descriptor.getClazz());
            Field field;
            Method getter, setter;
            for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals("class")) {
                    continue;
                }
                getter = propertyDescriptor.getReadMethod();
                setter = propertyDescriptor.getWriteMethod();
                field = descriptor.getClazz().getDeclaredField(propertyDescriptor.getName());
                if (setter != null && getter != null) {
                    descriptor.addColumnDescriptor(new ColumnDescriptor(field.getName(), field.getType(),
                            getter, setter, analyzeColumnName(field), analyzeColumnType(field)));
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
            columnName = configuration.getColumnNamingStrategy().toDatabaseFormat(field.getName());
        }
        return columnName;
    }

    private int analyzeColumnType(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        int type = -1;
        if (annotation != null) {
            type = annotation.type();
        }
        if (type == -1) {
            type = getSqlTypeWithJavaType(field.getType());
            if (type == -1) {
                throw new UnsupportedJavaTypeException(field.getType());
            }
        }
        return type;
    }

    private int getSqlTypeWithJavaType(Class javaType) {
        if (javaType.equals(String.class)) {
            return Types.VARCHAR;
        } else if (javaType.equals(Integer.class)) {
            return Types.INTEGER;
        }
        return -1;
    }
}