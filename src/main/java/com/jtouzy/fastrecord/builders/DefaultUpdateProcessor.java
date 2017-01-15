package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultUpdateConditionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultUpdateExpression;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;

@Process(value = UpdateExpression.class, priority = Priority.NATIVE)
public class DefaultUpdateProcessor<T> extends DefaultWriteProcessor<T,UpdateExpression> implements UpdateProcessor<T> {

    // =============================================================================
    // Constructors
    // =============================================================================

    @Inject
    public DefaultUpdateProcessor(WriterCache writerCache, EntityPool entityPool,
                                  FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Super classes overrides
    // =============================================================================

    @SuppressWarnings("unchecked")
    @Override
    public void initProcessor(Class<T> entityClass) {
        super.initProcessor(entityClass);
        for (ColumnDescriptor columnDescriptor : getEntityDescriptor().getIdColumnDescriptors()) {
            ConditionsHelper.addCondition(
                    getExpression().getConditionChain(),
                    ConditionChainOperator.AND,
                    new DefaultUpdateConditionWrapper(
                            new DefaultSimpleTableColumnExpression(
                                    columnDescriptor.getColumnType(),
                                    getExpression().getTarget(),
                                    columnDescriptor.getColumnName()),
                            ConditionOperator.EQUALS,
                            new DefaultConstantExpression(
                                    columnDescriptor.getColumnType(),
                                    String.valueOf(columnDescriptor.getTypeManager().convertToDatabase(
                                            getFinalValue(getTarget(), columnDescriptor))))));
        }
    }

    // =============================================================================
    // Abstract methods overrides
    // =============================================================================

    @Override
    protected UpdateExpression createExpression() {
        return new DefaultUpdateExpression(new DefaultSimpleTableExpression(getEntityDescriptor().getTableName()));
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

    // =============================================================================
    // Private methods
    // =============================================================================

    private Object getFinalValue(Object target, ColumnDescriptor columnDescriptor) {
        if (columnDescriptor.isRelated()) {
            return getFinalValue(getObjectValue(target, columnDescriptor), columnDescriptor.getRelatedColumn());
        } else {
            return getObjectValue(target, columnDescriptor);
        }
    }
}
