package com.jtouzy.fastrecord.entity;

import java.lang.reflect.Method;

public class ColumnDescriptor {
    private String propertyName;
    private Class propertyType;
    private Method propertyGetter;
    private Method propertySetter;
    private String columnName;
    private int columnType;

    public ColumnDescriptor(String propertyName, Class propertyType, Method propertyGetter, Method propertySetter,
                            String columnName, int columnType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getPropertyName() {
        return propertyName;
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

    public int getColumnType() {
        return columnType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('[');
        sb.append("propertyName='").append(propertyName).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append(", propertyGetter=").append(propertyGetter);
        sb.append(", propertySetter=").append(propertySetter);
        sb.append(", columnName='").append(columnName).append('\'');
        sb.append(", columnType=").append(columnType);
        sb.append(']');
        return sb.toString();
    }
}
