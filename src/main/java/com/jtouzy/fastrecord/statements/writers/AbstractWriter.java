package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public abstract class AbstractWriter<T extends WritableContext> implements Writer<T> {
    private final DbReadyStatementMetadata statementMetadata;
    private T context;
    private WriterCache writerCache;

    public AbstractWriter() {
        this.statementMetadata = new BaseDbReadyStatementMetadata("");
    }

    @Override
    public void refreshContext(T context, WriterCache writerCache) {
        this.context = context;
        this.writerCache = writerCache;
        this.statementMetadata.clear();
    }

    @Override
    public T getContext() {
        return context;
    }

    @Override
    public DbReadyStatementMetadata getResult() {
        return statementMetadata;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public boolean needsNewCache() {
        return false;
    }

    protected <C extends WritableContext> void mergeWriter(C context) {
        Writer<C> writer = writerCache.getWriter(context);
        writer.write();
        this.statementMetadata.merge(writer.getResult());
    }
}
