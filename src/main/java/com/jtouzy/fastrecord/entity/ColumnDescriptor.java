package com.jtouzy.fastrecord.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColumnDescriptor {
    private final Field propertyField;
    private final Method propertyGetter;
    private final Method propertySetter;
    private final String columnName;
    private final boolean id;
    private int columnType;
    private ColumnDescriptor relatedColumn;

    public ColumnDescriptor(Field propertyField, Method propertyGetter, Method propertySetter,
                            String columnName, int columnType, boolean id) {
        this.propertyField = propertyField;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
        this.columnName = columnName;
        this.columnType = columnType;
        this.id = id;
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
        return propertyField.getType();
    }

    public Field getPropertyField() {
        return propertyField;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public boolean isId() {
        return id;
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
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('[');
        sb.append("propertyName='").append(propertyField.getName()).append('\'');
        sb.append(", propertyType=").append(propertyField.getType());
        sb.append(", propertyGetter=").append(propertyGetter);
        sb.append(", propertySetter=").append(propertySetter);
        sb.append(", columnName='").append(columnName).append('\'');
        sb.append(", columnType=").append(columnType);
        sb.append(", id=").append(id);
        sb.append(']');
        return sb.toString();
    }
}
