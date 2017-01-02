package com.jtouzy.fastrecord.builders;

public interface WriteProcessor<T> {
    void init(Class<T> entityClass, T target);
    void execute() throws StatementException;
}
