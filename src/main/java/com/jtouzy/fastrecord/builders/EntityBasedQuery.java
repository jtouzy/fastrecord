package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDefinitionException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.reflect.ResultSetObjectMaker;
import com.jtouzy.fastrecord.statements.context.BaseAliasTableColumnContext;
import com.jtouzy.fastrecord.statements.context.BaseConditionContext;
import com.jtouzy.fastrecord.statements.context.BaseQueryContext;
import com.jtouzy.fastrecord.statements.context.BaseTableAliasContext;
import com.jtouzy.fastrecord.statements.context.BaseTableColumnContext;
import com.jtouzy.fastrecord.statements.context.ConditionContext;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionsOperator;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.statements.writers.WriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
public class EntityBasedQuery<T> {
    private static final Logger logger = LoggerFactory.getLogger(EntityBasedQuery.class);

    @Autowired
    private EntityPool entityPool;
    @Autowired
    private WriterFactory writerFactory;
    @Autowired
    private WriterCache writerCache;
    @Autowired
    private DataSource dataSource;

    EntityDescriptor entityDescriptor;
    QueryContext queryContext;
    private ConditionsConfigurer<T> conditionsConfigurer;

    private EntityBasedQuery() {
    }

    void fromClass(Class<T> entityClass) {
        checkEntity(entityClass);
        initializeContext();
    }

    private EntityDescriptor findEntityDescriptorWithClass(Class entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        return entityDescriptorOptional.get();
    }

    private void checkEntity(Class<T> entityClass) {
        this.entityDescriptor = findEntityDescriptorWithClass(entityClass);
    }

    private void initializeContext() {
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
        return writerFactory.getWriter(writerCache, queryContext).write();
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

    public EntityBasedQuery<T> fill(Class filledEntityClass) {
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

    public List<T> findAll() {
        ResultSet rs;
        try {
            Connection connection = dataSource.getConnection();
            DbReadyStatementMetadata metadata = writeMetadata();
            String sqlString = metadata.getSqlString().toString();
            logger.debug("SQL String [{}]", sqlString);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
            int index = 1;
            for (DbReadyStatementParameter parameter : metadata.getParameters()) {
                preparedStatement.setObject(index, parameter.getValue(), parameter.getType());
                index ++;
            }
            rs = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            throw new QueryException(ex);
        }
        ResultSetObjectMaker<T> objectMaker = new ResultSetObjectMaker<>(queryContext, entityDescriptor, rs);
        return objectMaker.make();
    }
}
