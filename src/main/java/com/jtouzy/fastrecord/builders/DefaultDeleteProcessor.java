package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.DeleteExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultDeleteExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;

@Process(value = DeleteExpression.class, priority = Priority.NATIVE)
public class DefaultDeleteProcessor<T>
        extends DefaultWriteWithConditionsProcessor<T,DeleteExpression> implements DeleteProcessor<T> {

    // =============================================================================
    // Constructors
    // =============================================================================

    @Inject
    public DefaultDeleteProcessor(WriterCache writerCache, EntityPool entityPool,
                                  FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Abstract methods overrides
    // =============================================================================

    @Override
    protected DeleteExpression createExpression() {
        return new DefaultDeleteExpression(new DefaultSimpleTableExpression(getEntityDescriptor().getTableName()));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addColumnExpression(ColumnDescriptor columnDescriptor, String columnName,
                                       SimpleTableExpression tableExpression, Object columnValue) {
    }
}
