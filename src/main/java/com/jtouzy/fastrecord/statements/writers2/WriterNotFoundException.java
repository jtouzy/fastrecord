package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.statements.context2.WritableContext;

public class WriterNotFoundException extends RuntimeException {
    public WriterNotFoundException(Class<? extends WritableContext> writableContext) {
        super("No writer found for " + writableContext);
    }
}
