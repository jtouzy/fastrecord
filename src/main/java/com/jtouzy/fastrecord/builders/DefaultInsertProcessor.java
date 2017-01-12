package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultInsertExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Process(value = InsertExpression.class, priority = Priority.NATIVE)
public class DefaultInsertProcessor<T> extends DefaultProcessor<T,InsertExpression> implements InsertProcessor<T> {

    // =============================================================================
    // Attributes
    // =============================================================================

    private T target;

    // =============================================================================
    // Constructors
    // =============================================================================

    @Autowired
    public DefaultInsertProcessor(WriterCache writerCache, EntityPool entityPool,
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
    // Abstract methods overrides
    // =============================================================================

    @Override
    protected InsertExpression createExpression() {
        return new DefaultInsertExpression(new DefaultSimpleTableExpression(getEntityDescriptor().getTableName()));
    }

    // =============================================================================
    // Super classes overrides
    // =============================================================================

    @Override
    public void initProcessor(Class<T> entityClass) {
        super.initProcessor(entityClass);
        for (ColumnDescriptor columnDescriptor : getEntityDescriptor().getColumnDescriptors()) {
            addInsertValueWithColumn(target, columnDescriptor,
                    columnDescriptor.getColumnName(), getExpression().getTarget());
        }
    }

    // =============================================================================
    // Protected methods
    // =============================================================================

    protected T getTarget() {
        return target;
    }

    // =============================================================================
    // Private methods
    // =============================================================================

    protected void refreshEntity(PreparedStatement preparedStatement)
    throws SQLException, StatementException {
    }

    private void addInsertValueWithColumn(Object target, ColumnDescriptor columnDescriptor, String columnName,
                                          SimpleTableExpression tableExpression) {
        if (target == null) {
            return;
        }
        if (columnDescriptor.isRelated()) {
            addInsertValueWithColumn(
                    getObjectValue(target, columnDescriptor),
                    columnDescriptor.getRelatedColumn(), columnName, tableExpression);
        } else {
            addInsertValue(target, columnDescriptor, columnName, tableExpression);
        }
    }

    @SuppressWarnings("unchecked")
    private void addInsertValue(Object target, ColumnDescriptor columnDescriptor, String columnName,
                                SimpleTableExpression tableExpression) {
        Object columnValue = getObjectValue(target, columnDescriptor);
        if (columnValue == null) {
            return;
        }
        getExpression().getValues().put(
                new DefaultSimpleTableColumnExpression(
                        columnDescriptor.getColumnType(), tableExpression, columnName),
                String.valueOf(columnDescriptor.getTypeManager().convertToDatabase(columnValue)));
    }

    private Object getObjectValue(Object target, ColumnDescriptor columnDescriptor) {
        try {
            return columnDescriptor.getPropertyGetter().invoke(target);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new ObjectReadException(ex);
        }
    }
}