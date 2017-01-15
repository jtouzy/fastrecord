package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultInsertExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;

@Process(value = InsertExpression.class, priority = Priority.NATIVE)
public class DefaultInsertProcessor<T> extends DefaultWriteProcessor<T,InsertExpression> implements InsertProcessor<T> {

    // =============================================================================
    // Constructors
    // =============================================================================

    @Inject
    public DefaultInsertProcessor(WriterCache writerCache, EntityPool entityPool,
                                  FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Abstract methods overrides
    // =============================================================================

    @Override
    protected InsertExpression createExpression() {
        return new DefaultInsertExpression(new DefaultSimpleTableExpression(getEntityDescriptor().getTableName()));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addColumnExpression(ColumnDescriptor columnDescriptor, String columnName,
                                       SimpleTableExpression tableExpression, Object columnValue) {
        getExpression().getValues().put(
                new DefaultSimpleTableColumnExpression(
                        columnDescriptor.getColumnType(), tableExpression, columnName),
                new DefaultConstantExpression(
                        columnDescriptor.getColumnType(),
                        String.valueOf(columnDescriptor.getTypeManager().convertToDatabase(columnValue))));
    }
}
