package com.jtouzy.fastrecord.statements.writers;

public class WriterDefinitionException extends RuntimeException {
    public WriterDefinitionException(String message) {
        super(message);
    }

    public WriterDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
