package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.context.WriteExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DefaultWriteProcessor<T,E extends WritableContext & WriteExpression>
        extends DefaultProcessor<T,E> implements WriteProcessor<T,E> {

    // =============================================================================
    // Attributes
    // =============================================================================

    private T target;

    // =============================================================================
    // Constructors
    // =============================================================================

    public DefaultWriteProcessor(WriterCache writerCache, EntityPool entityPool,
                                 FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Initialization overrides
    // =============================================================================

    @Override
    public void initWriteProcessor(Class<T> entityClass, T target) {
        this.target = target;
        initProcessor(entityClass);
    }

    // =============================================================================
    // Interface overrides
    // =============================================================================

    @Override
    public T execute() throws StatementException {
        DbReadyStatementMetadata metadata = writeMetadata();
        String sqlString = metadata.getSqlString().toString();
        printSql(metadata);
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlString)) {
            int index = 1;
            for (DbReadyStatementParameter parameter : metadata.getParameters()) {
                preparedStatement.setObject(index, parameter.getValue(), parameter.getType());
                index ++;
            }
            preparedStatement.execute();
            refreshEntity(preparedStatement);
            return target;
        } catch (SQLException ex) {
            throw new StatementException(ex);
        }
    }

    // =============================================================================
    // Super classes overrides
    // =============================================================================

    @Override
    public void initProcessor(Class<T> entityClass) {
        super.initProcessor(entityClass);
        for (ColumnDescriptor columnDescriptor : getEntityDescriptor().getColumnDescriptors()) {
            addValueWithColumn(getTarget(), columnDescriptor,
                    columnDescriptor.getColumnName(), getExpression().getTarget());
        }
    }

    // =============================================================================
    // Protected methods
    // =============================================================================

    protected T getTarget() {
        return target;
    }

    protected void refreshEntity(PreparedStatement preparedStatement)
    throws SQLException, StatementException {
    }

    protected Object getObjectValue(Object target, ColumnDescriptor columnDescriptor) {
        try {
            return columnDescriptor.getPropertyGetter().invoke(target);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
            throw new ObjectReadException("Error when read " + columnDescriptor.getPropertyName() + " on object " + target, ex);
        }
    }

    // =============================================================================
    // Abstract methods
    // =============================================================================

    protected abstract void addColumnExpression(ColumnDescriptor columnDescriptor, String columnName,
                                                SimpleTableExpression tableExpression, Object columnValue);

    // =============================================================================
    // Private methods
    // =============================================================================

    private void addValueWithColumn(Object target, ColumnDescriptor columnDescriptor, String columnName,
                                    SimpleTableExpression tableExpression) {
        if (target == null) {
            return;
        }
        if (columnDescriptor.isRelated()) {
            addValueWithColumn(
                    getObjectValue(target, columnDescriptor),
                    columnDescriptor.getRelatedColumn(), columnName, tableExpression);
        } else {
            addExpressionValue(target, columnDescriptor, columnName, tableExpression);
        }
    }

    @SuppressWarnings("unchecked")
    private void addExpressionValue(Object target, ColumnDescriptor columnDescriptor, String columnName,
                                    SimpleTableExpression tableExpression) {
        Object columnValue = getObjectValue(target, columnDescriptor);
        if (columnValue == null) {
            return;
        }
        addColumnExpression(columnDescriptor, columnName, tableExpression, columnValue);
    }
}
