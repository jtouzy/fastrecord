package com.jtouzy.fastrecord.entity;

import com.jtouzy.fastrecord.entity.types.TypeManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColumnDescriptor {
    private final Field propertyField;
    private final Class propertyType;
    private final Method propertyGetter;
    private final Method propertySetter;
    private final String columnName;
    private final boolean id;
    private final boolean generated;
    private TypeManager typeManager;
    private ColumnDescriptor relatedColumn;

    public ColumnDescriptor(Field propertyField, Class propertyType, TypeManager typeManager, Method propertyGetter,
                            Method propertySetter, String columnName, boolean id, boolean generated) {
        this.propertyField = propertyField;
        this.propertyType = propertyType;
        this.typeManager = typeManager;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
        this.columnName = columnName;
        this.id = id;
        this.generated = generated;
    }

    public String getPropertyName() {
        return propertyField.getName();
    }

    public Method getPropertyGetter() {
        return propertyGetter;
    }

    public Method getPropertySetter() {
        return propertySetter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class getPropertyType() {
        return propertyType;
    }

    public Field getPropertyField() {
        return propertyField;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public int getColumnType() {
        return typeManager.getSqlType();
    }

    public boolean isId() {
        return id;
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isRelated() {
        return this.relatedColumn != null;
    }

    public ColumnDescriptor getRelatedColumn() {
        return relatedColumn;
    }

    public void setRelatedColumn(ColumnDescriptor relatedColumn) {
        this.relatedColumn = relatedColumn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("@").append(Integer.toHexString(hashCode()));
        sb.append('[');
        sb.append("propertyName='").append(propertyField.getName()).append('\'');
        sb.append(", propertyType=").append(propertyField.getType());
        sb.append(", propertyGetter=").append(propertyGetter);
        sb.append(", propertySetter=").append(propertySetter);
        sb.append(", columnName='").append(columnName).append('\'');
        sb.append(", typeManager=").append(typeManager);
        sb.append(", id=").append(id);
        sb.append(']');
        return sb.toString();
    }
}
