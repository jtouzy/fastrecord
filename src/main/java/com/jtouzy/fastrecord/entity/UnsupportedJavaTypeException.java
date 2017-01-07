package com.jtouzy.fastrecord.entity;

public class UnsupportedJavaTypeException extends RuntimeException {
    public UnsupportedJavaTypeException(Class type, Class entityClass) {
        super("Unsupported Java Type [" + type + "] in [" + entityClass + "]");
    }
}
