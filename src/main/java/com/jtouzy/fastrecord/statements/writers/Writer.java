package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public interface Writer<T> {
    T getContext();
    void setContext(T context);
    DbReadyStatementMetadata write();
    void clear();
}
