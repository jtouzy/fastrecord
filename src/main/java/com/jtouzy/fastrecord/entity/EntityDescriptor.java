package com.jtouzy.fastrecord.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityDescriptor {
    private final Class clazz;
    private final String tableName;
    private final List<ColumnDescriptor> columnDescriptors;

    public EntityDescriptor(Class clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.columnDescriptors = new ArrayList<>();
    }

    public Class getClazz() {
        return clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public void addColumnDescriptor(ColumnDescriptor columnDescriptor) {
        this.columnDescriptors.add(columnDescriptor);
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return columnDescriptors;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append('[');
        sb.append("clazz=").append(clazz);
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", columnCount='").append(columnDescriptors.size()).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
