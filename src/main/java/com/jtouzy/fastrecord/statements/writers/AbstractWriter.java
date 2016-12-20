package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.FastRecord;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;

public abstract class AbstractWriter<T> implements Writer<T> {
    private final T context;
    private final DbReadyStatementMetadata statementMetadata;

    public AbstractWriter(T context) {
        this.context = context;
        this.statementMetadata = new BaseDbReadyStatementMetadata("");
    }

    @Override
    public T getContext() {
        return context;
    }

    protected StringBuilder getSqlString() {
        return statementMetadata.getSqlString();
    }

    protected void addParameter(DbReadyStatementParameter parameter) {
        statementMetadata.addParameter(parameter);
    }

    protected DbReadyStatementMetadata buildMetadata() {
        return statementMetadata;
    }

    protected void mergeWriter(Object contextObject) {
        // TODO optimizations to not search class and store class/constructor for each context called
        Writer writer = FastRecord.fr().getWriterFactory().getWriter(contextObject);
        DbReadyStatementMetadata statementMetadata = writer.write();
        this.statementMetadata.merge(statementMetadata);
    }
}
