package com.jtouzy.fastrecord.entity;

public class UnsupportedJavaTypeException extends RuntimeException {
    public UnsupportedJavaTypeException(Class type) {
        super("Unsupported Java Type : " + type);
    }
}
