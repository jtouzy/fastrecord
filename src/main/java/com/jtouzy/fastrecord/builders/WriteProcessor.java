package com.jtouzy.fastrecord.builders;

public interface WriteProcessor<T> extends Processor {
    void init(Class<T> entityClass, T target);
    T execute() throws StatementException;
}
