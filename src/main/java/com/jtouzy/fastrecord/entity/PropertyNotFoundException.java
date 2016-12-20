package com.jtouzy.fastrecord.entity;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(String propertyName, Class entityClass) {
        super("Property " + propertyName + " is not registered in the entity class " + entityClass);
    }
}
