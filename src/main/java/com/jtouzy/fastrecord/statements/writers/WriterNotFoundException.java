package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.WritableContext;

public class WriterNotFoundException extends RuntimeException {
    public WriterNotFoundException(Class<? extends WritableContext> writableContext) {
        super("No writer found for " + writableContext);
    }
}
