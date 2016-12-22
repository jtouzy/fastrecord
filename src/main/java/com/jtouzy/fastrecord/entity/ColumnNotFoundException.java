package com.jtouzy.fastrecord.entity;

public class ColumnNotFoundException extends RuntimeException {
    public ColumnNotFoundException(String columnName, Class entityClass) {
        super("Table column [" + columnName + "] is not registered in the entity class [" + entityClass + "]");
    }
}
