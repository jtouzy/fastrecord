package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.FastRecord;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDefinitionException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.statements.context.*;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

import java.util.List;
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

    private EntityDescriptor findEntityDescriptorWithClass(Class entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                FastRecord.fr().getEntityPool().getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        return entityDescriptorOptional.get();
    }

    private void checkEntity(Class<T> entityClass) {
        this.entityDescriptor = findEntityDescriptorWithClass(entityClass);
    }

    private void initializeContext() {
        this.writerCache = new WriterCache();
        this.conditionsConfigurer = new ConditionsConfigurer<>(this);
        createQueryContextWithEntity();
    }

    private void createQueryContextWithEntity() {
        queryContext = new BaseQueryContext();
        createQueryColumnsWithEntity(entityDescriptor);
        queryContext.addFromContext(new BaseTableAliasContext("", entityDescriptor.getTableName()));
    }

    private void createQueryColumnsWithEntity(EntityDescriptor entityDescriptor) {
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            queryContext.addColumnContext(new BaseAliasTableColumnContext("", "",
                    entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                    columnDescriptor.getColumnType()));
        }
    }

    private DbReadyStatementMetadata writeMetadata() {
        return FastRecord.fr().getWriterFactory().getWriter(writerCache, queryContext).write();
    }

    private List<ColumnDescriptor> safeGetColumnsRelatedToFilled(Class relatedClass) {
        List<ColumnDescriptor> columnsRelatedToFilled = entityDescriptor.getColumnDescriptorsWithType(relatedClass);
        if (columnsRelatedToFilled.size() == 0) {
            throw new EntityDefinitionException("Entity [" + entityDescriptor.getClazz() +
                    "] does not have relation to [" + relatedClass + "]");
        }
        if (columnsRelatedToFilled.size() > 1) {
            throw new EntityDefinitionException("Entity [" + entityDescriptor.getClazz() +
                    "] have mutliple relation to [" + relatedClass + "] : Not implemented");
        }
        return columnsRelatedToFilled;
    }

    private void addSimpleJoinConditions(EntityDescriptor relatedDescriptor, List<ColumnDescriptor> columnsRelatedToFilled) {
        ColumnDescriptor columnRelatedToFilled = columnsRelatedToFilled.get(0);
        ConditionContext condition = new BaseConditionContext(ConditionOperator.EQUALS);
        condition.addFirstExpression(new BaseTableColumnContext("", entityDescriptor.getTableName(),
                columnRelatedToFilled.getColumnName(), columnRelatedToFilled.getColumnType()));
        List<ColumnDescriptor> idColumns = relatedDescriptor.getIdColumnDescriptors();
        if (idColumns.size() == 0) {
            // TODO ce contrôle n'est pas à faire puisque logiquement, on a trouvé l'entité, donc il doit y avoir
            // TODO au moins 1 ID dans l'entité, EntityValidator fera ce contrôle
        }
        if (idColumns.size() > 1) {
            throw new EntityDefinitionException("Related entity [" + relatedDescriptor.getClazz() +
                    "] describes more than one ID column : Not implemented");
        }
        ColumnDescriptor relatedColumn = idColumns.get(0);
        condition.addCompareExpression(new BaseTableColumnContext("", relatedDescriptor.getTableName(),
                relatedColumn.getColumnName(), relatedColumn.getColumnType()));
        queryContext.getConditionsContext().addConditionContext(ConditionsOperator.AND, condition);
    }

    public Query<T> fill(Class filledEntityClass) {
        // Safe checks
        EntityDescriptor relatedDescriptor = findEntityDescriptorWithClass(filledEntityClass);
        List<ColumnDescriptor> columnsRelatedToFilled = safeGetColumnsRelatedToFilled(filledEntityClass);
        // Context creation
        createQueryColumnsWithEntity(relatedDescriptor);
        queryContext.addFromContext(JoinOperator.JOIN, new BaseTableAliasContext("", relatedDescriptor.getTableName()));
        addSimpleJoinConditions(relatedDescriptor, columnsRelatedToFilled);
        return this;
    }

    public ConditionsConfigurer<T> conditions() {
        return conditionsConfigurer;
    }

    public String getSql() {
        return writeMetadata().getSqlString().toString();
    }
}
