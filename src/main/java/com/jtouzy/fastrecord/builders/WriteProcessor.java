package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.WritableContext;

public interface WriteProcessor<T,E extends WritableContext> extends Processor<T,E> {
    void initWriteProcessor(Class<T> entityClass, T target);
    T execute() throws StatementException;
}
