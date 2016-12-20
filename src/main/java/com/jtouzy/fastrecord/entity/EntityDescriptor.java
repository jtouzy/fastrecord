package com.jtouzy.fastrecord.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityDescriptor {
    private final Class clazz;
    private final String tableName;
    private final Map<String,ColumnDescriptor> columnDescriptorsByProperty;

    public EntityDescriptor(Class clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.columnDescriptorsByProperty = new HashMap<>();
    }

    public Class getClazz() {
        return clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public void addColumnDescriptor(ColumnDescriptor columnDescriptor) {
        this.columnDescriptorsByProperty.put(columnDescriptor.getPropertyName(), columnDescriptor);
    }

    public Optional<ColumnDescriptor> getColumnDescriptor(String propertyName) {
        return Optional.ofNullable(columnDescriptorsByProperty.get(propertyName));
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return new ArrayList<>(columnDescriptorsByProperty.values());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('[');
        sb.append("clazz=").append(clazz);
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", columnCount='").append(columnDescriptorsByProperty.size()).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
