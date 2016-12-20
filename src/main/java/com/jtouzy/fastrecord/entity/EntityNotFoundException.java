package com.jtouzy.fastrecord.entity;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class entityClass) {
        super("[" + entityClass + "] is not associated to an Entity");
    }
}
