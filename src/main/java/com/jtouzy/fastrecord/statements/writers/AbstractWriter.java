package com.jtouzy.fastrecord.statements.writers;

public abstract class AbstractWriter<T> implements Writer<T> {
    private final T context;
    private final StringBuilder sqlString;

    public AbstractWriter(T context) {
        this.context = context;
        this.sqlString = new StringBuilder();
    }

    @Override
    public StringBuilder getSqlString() {
        return sqlString;
    }

    @Override
    public T getContext() {
        return context;
    }

    protected void appendSqlStringFromWriter(Object contextObject) {
        // TODO optimizations to not search class and store class/constructor for each context called
        // TODO something to clone the DbReady result into another
        Writer writer = WriterFactory.getWriter(contextObject);
        writer.write();
        sqlString.append(writer.getSqlString());
    }
}
