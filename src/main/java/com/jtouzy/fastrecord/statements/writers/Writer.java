package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public interface Writer<T> {
    T getContext();
    DbReadyStatementMetadata write();
}
