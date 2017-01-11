package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChainHolder;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

public abstract class EntityWriteProcessor<T, E extends WritableContext & ConditionChainHolder>
        extends EntityBasedConditionsProcessor<T,E> implements WriteProcessor<T> {

    protected T target;

    public EntityWriteProcessor(EntityPool entityPool, WriterCache writerCache, FastRecordConfiguration configuration) {
        super(entityPool, writerCache, configuration);
    }

    @Override
    public void init(Class<T> entityClass, T target) {
        super.init(entityClass);
        this.target = target;
    }
}
