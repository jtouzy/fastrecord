package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWriter<T> implements Writer<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractWriter.class);
    private final WriterCache writerCache;
    private final DbReadyStatementMetadata statementMetadata;
    private T context;

    public AbstractWriter(WriterCache writerCache, T context) {
        this.writerCache = writerCache;
        this.context = context;
        this.statementMetadata = new BaseDbReadyStatementMetadata("");
    }

    @Override
    public T getContext() {
        return context;
    }

    @Override
    public void setContext(T context) {
        this.context = context;
    }

    public WriterCache getWriterCache() {
        return writerCache;
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
        logger.debug("Start processing [merge] with [{}]...", contextObject);
        Writer writer = getWriterCache().getWriter(contextObject);
        DbReadyStatementMetadata statementMetadata = writer.write();
        this.statementMetadata.merge(statementMetadata);
        writer.clear();
    }

    @Override
    public DbReadyStatementMetadata write() {
        logger.debug("Writing [{}] with [{}]", context, this);
        return null;
    }

    @Override
    public void clear() {
        this.statementMetadata.clear();
    }
}
