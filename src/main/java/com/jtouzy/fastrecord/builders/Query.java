package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.FastRecord;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.statements.context.BaseAliasTableColumnContext;
import com.jtouzy.fastrecord.statements.context.BaseQueryContext;
import com.jtouzy.fastrecord.statements.context.BaseTableAliasContext;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

import java.util.Optional;

public class Query<T> {
    public static <T> Query<T> from(Class<T> entityClass) {
        return new Query<>(entityClass);
    }

    protected EntityDescriptor entityDescriptor;
    private WriterCache writerCache;
    protected QueryContext queryContext;
    private ConditionsConfigurer<T> conditionsConfigurer;

    private Query(Class<T> entityClass) {
        checkEntity(entityClass);
        initializeContext();
    }

    private void checkEntity(Class<T> entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                FastRecord.fr().getEntityPool().getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        this.entityDescriptor = entityDescriptorOptional.get();
    }

    private void initializeContext() {
        this.writerCache = new WriterCache();
        this.conditionsConfigurer = new ConditionsConfigurer<>(this);
        createQueryContextWithEntity();
    }

    private void createQueryContextWithEntity() {
        queryContext = new BaseQueryContext();
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            queryContext.addColumnContext(new BaseAliasTableColumnContext("", "",
                    entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                    columnDescriptor.getColumnType()));
        }
        queryContext.addFromContext(new BaseTableAliasContext("", entityDescriptor.getTableName()));
    }

    private DbReadyStatementMetadata writeMetadata() {
        return FastRecord.fr().getWriterFactory().getWriter(writerCache, queryContext).write();
    }

    public ConditionsConfigurer<T> conditions() {
        return conditionsConfigurer;
    }

    public String getSql() {
        return writeMetadata().getSqlString().toString();
    }
}
