package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import javax.sql.DataSource;

public interface Processor<T, E extends WritableContext> {
    void initProcessor(Class<T> entityClass);

    void setDataSource(DataSource dataSource);
    E getExpression();
    EntityDescriptor getEntityDescriptor();

    String getSql();
    DbReadyStatementMetadata writeMetadata();
}
