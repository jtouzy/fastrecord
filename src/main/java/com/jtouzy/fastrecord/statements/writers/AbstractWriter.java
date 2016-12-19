package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public abstract class AbstractWriter<T> implements Writer<T> {
    private final T context;
    private final StringBuilder sqlString;

    public AbstractWriter(T context) {
        this.context = context;
        this.sqlString = new StringBuilder();
    }

    @Override
    public T getContext() {
        return context;
    }

    public StringBuilder getSqlString() {
        return sqlString;
    }

    protected void appendSqlStringFromWriter(Object contextObject) {
        // TODO optimizations to not search class and store class/constructor for each context called
        // TODO something to clone the DbReady result into another
        Writer writer = WriterFactory.getWriter(contextObject);
        DbReadyStatementMetadata statementMetadata = writer.write();
        sqlString.append(statementMetadata.getSqlString());
    }
}
