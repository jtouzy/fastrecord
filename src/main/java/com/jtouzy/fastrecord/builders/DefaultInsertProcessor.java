package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConstantExpression;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultInsertExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Process(value = InsertExpression.class, priority = Priority.NATIVE)
public class DefaultInsertProcessor<T> extends DefaultWriteProcessor<T,InsertExpression> implements InsertProcessor<T> {
    private Map<SimpleTableColumnExpression,ConstantExpression> currentValues;
    private boolean multipleInsert = false;

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
        currentValues.put(
                new DefaultSimpleTableColumnExpression(
                        columnDescriptor.getColumnType(), tableExpression, columnName),
                new DefaultConstantExpression(
                        columnDescriptor.getColumnType(),
                        String.valueOf(columnDescriptor.getTypeManager().convertToDatabase(columnValue))));
    }

    @Override
    protected void initTargetValues(T target) {
        currentValues = new LinkedHashMap<>();
        getExpression().getValues().add(currentValues);
        super.initTargetValues(target);
    }

    @Override
    protected void initDefaultValues() {
        if (!multipleInsert) {
            super.initDefaultValues();
        }
    }

    // =============================================================================
    // Interface overrides
    // =============================================================================

    @Override
    public void initProcessor(Class<T> entityClass, List<T> targetList) {
        multipleInsert = targetList.size() > 1;
        super.initProcessor(entityClass);
        if (multipleInsert) {
            for (T object : targetList) {
                initTargetValues(object);
            }
        }
    }
}
