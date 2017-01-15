package com.jtouzy.fastrecord.builders;

public class ObjectReadException extends RuntimeException {
    public ObjectReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectReadException(Throwable cause) {
        super(cause);
    }
}
