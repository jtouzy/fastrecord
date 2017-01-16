package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultUpdateExpression;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;

@Process(value = UpdateExpression.class, priority = Priority.NATIVE)
public class DefaultUpdateProcessor<T>
        extends DefaultWriteWithConditionsProcessor<T,UpdateExpression> implements UpdateProcessor<T> {

    // =============================================================================
    // Constructors
    // =============================================================================

    @Inject
    public DefaultUpdateProcessor(WriterCache writerCache, EntityPool entityPool,
                                  FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Abstract methods overrides
    // =============================================================================

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

    @Override
    protected UpdateExpression createExpression() {
        return new DefaultUpdateExpression(new DefaultSimpleTableExpression(getEntityDescriptor().getTableName()));
    }
}
