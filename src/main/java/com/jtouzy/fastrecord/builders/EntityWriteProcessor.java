package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChainHolder;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

public abstract class EntityWriteProcessor<T, E extends WritableContext & ConditionChainHolder>
        extends EntityBasedConditionsProcessor<T,E> {

    protected T target;

    public EntityWriteProcessor(EntityPool entityPool, WriterCache writerCache, FastRecordConfiguration configuration) {
        super(entityPool, writerCache, configuration);
    }

    public void init(Class<T> entityClass, T target) {
        init(entityClass);
        this.target = target;
    }

    protected abstract void execute() throws StatementException;
}
