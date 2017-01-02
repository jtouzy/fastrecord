package com.jtouzy.fastrecord.builders;

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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("FastRecord.Core.EntityInsertProcessor")
@Scope("prototype")
public class EntityInsertProcessor<T> extends EntityBasedProcessor<T,InsertExpression> implements WriteProcessor<T> {
    protected T target;

    public EntityInsertProcessor(EntityPool entityPool, WriterCache writerCache,
                                 FastRecordConfiguration configuration) {
        super(entityPool, writerCache, configuration);
    }

    @Override
    public void execute() throws StatementException {
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
        } catch (SQLException ex) {
            throw new StatementException(ex);
        }
    }

    @Override
    public void init(Class<T> entityClass, T target) {
        this.target = target;
        init(entityClass);
    }

    @Override
    protected void initializeContext() {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression(getEntityDescriptor().getTableName());
        expression = new DefaultInsertExpression(tableExpression);
        for (ColumnDescriptor columnDescriptor : getEntityDescriptor().getColumnDescriptors()) {
            addInsertValueWithColumn(target, columnDescriptor, tableExpression);
        }
    }

    private void addInsertValueWithColumn(Object target, ColumnDescriptor columnDescriptor,
                                          SimpleTableExpression tableExpression) {
        if (target == null) {
            return;
        }
        if (!columnDescriptor.isRelated()) {
            addInsertValue(target, columnDescriptor, tableExpression);
        } else {
            addInsertValueWithColumn(
                    getObjectValue(target, columnDescriptor), columnDescriptor.getRelatedColumn(), tableExpression);
        }
    }

    private void addInsertValue(Object target, ColumnDescriptor columnDescriptor,
                                SimpleTableExpression tableExpression) {
        Object columnValue = getObjectValue(target, columnDescriptor);
        if (columnValue == null) {
            return;
        }
        expression.getValues().put(
                new DefaultSimpleTableColumnExpression(
                        columnDescriptor.getColumnType(), tableExpression, columnDescriptor.getColumnName()),
                String.valueOf(columnValue));
    }

    private Object getObjectValue(Object target, ColumnDescriptor columnDescriptor) {
        try {
            return columnDescriptor.getPropertyGetter().invoke(target);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new ObjectReadException(ex);
        }
    }
}
