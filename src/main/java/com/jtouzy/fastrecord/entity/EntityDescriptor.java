package com.jtouzy.fastrecord.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityDescriptor {
    private final Class clazz;
    private final String tableName;
    private final Map<String,ColumnDescriptor> columnDescriptorsByColumn;

    public EntityDescriptor(Class clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.columnDescriptorsByColumn = new HashMap<>();
    }

    public Class getClazz() {
        return clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public void addColumnDescriptor(ColumnDescriptor columnDescriptor) {
        ColumnDescriptor oldDescriptor = columnDescriptorsByColumn.get(columnDescriptor.getColumnName());
        if (oldDescriptor == null) {
            columnDescriptorsByColumn.put(columnDescriptor.getColumnName(), columnDescriptor);
        } else {
            if (oldDescriptor.isId() && !columnDescriptor.isId()) {
                // Do not replace the descriptor if it's an ID column referenced in another table column
                // In this case the first column is the only one to add
            } else {
                columnDescriptorsByColumn.put(columnDescriptor.getColumnName(), columnDescriptor);
            }
        }
    }

    public void removeColumnDescriptor(ColumnDescriptor columnDescriptor) {
        this.columnDescriptorsByColumn.remove(columnDescriptor.getColumnName());
    }

    public List<ColumnDescriptor> getColumnDescriptorsRelatedWith(ColumnDescriptor relatedColumn) {
        return columnDescriptorsByColumn.values().stream()
                .filter(r -> relatedColumn.equals(r.getRelatedColumn())).collect(Collectors.toList());
    }

    public List<ColumnDescriptor> getColumnDescriptorsByProperty(String propertyName) {
        return columnDescriptorsByColumn.values().stream()
                .filter(d -> d.getPropertyName().equals(propertyName)).collect(Collectors.toList());
    }

    public Optional<ColumnDescriptor> getColumnDescriptorByColumn(String columnName) {
        return Optional.ofNullable(columnDescriptorsByColumn.get(columnName));
    }

    public List<ColumnDescriptor> getDistinctColumnDescriptorsWithType(Class propertyType) {
        List<String> properties = new ArrayList<>();
        return columnDescriptorsByColumn.values().stream()
                .filter(p -> {
                    if (p.getPropertyType().equals(propertyType)) {
                        if (!properties.contains(p.getPropertyName())) {
                            properties.add(p.getPropertyName());
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return new ArrayList<>(columnDescriptorsByColumn.values());
    }

    public List<ColumnDescriptor> getIdColumnDescriptors() {
        return columnDescriptorsByColumn.values().stream()
                .filter(ColumnDescriptor::isId).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("@").append(Integer.toHexString(hashCode()));
        sb.append('[');
        sb.append("clazz=").append(clazz);
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", columnCount='").append(columnDescriptorsByColumn.size()).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
