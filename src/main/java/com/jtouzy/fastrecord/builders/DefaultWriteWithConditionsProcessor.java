package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChainHolder;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.WriteExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultUpdateConditionWrapper;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

public abstract class DefaultWriteWithConditionsProcessor<T,E extends WriteExpression & ConditionChainHolder>
        extends DefaultWriteProcessor<T,E> {

    // =============================================================================
    // Constructors
    // =============================================================================

    public DefaultWriteWithConditionsProcessor(WriterCache writerCache, EntityPool entityPool,
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
