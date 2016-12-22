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
import com.jtouzy.fastrecord.statements.context.TableAliasContext;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    String firstEntityDescriptorAlias;
    QueryContext queryContext;
    private ConditionsConfigurer<T> conditionsConfigurer;
    private Map<String,EntityDescriptor> entityDescriptorsByAlias;
    private Map<ColumnDescriptor,String> columnDescriptorAliasMapping;

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
        this.entityDescriptorsByAlias = new HashMap<>();
        this.columnDescriptorAliasMapping = new HashMap<>();
        createQueryContextWithEntity();
    }

    private void createQueryContextWithEntity() {
        queryContext = new BaseQueryContext();
        firstEntityDescriptorAlias = createEntityDescriptorContext(entityDescriptor);
    }

    private String createQueryColumnsWithEntity(EntityDescriptor entityDescriptor) {
        String tableAlias = registerAlias(entityDescriptor);
        String columnAlias;
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            columnAlias = tableAlias + "_" + columnDescriptor.getColumnName();
            queryContext.addColumnContext(new BaseAliasTableColumnContext(columnAlias, tableAlias,
                    entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                    columnDescriptor.getColumnType()));
        }
        return tableAlias;
    }

    private String createEntityDescriptorContext(JoinOperator joinOperator, EntityDescriptor entityDescriptor) {
        String tableAlias = createQueryColumnsWithEntity(entityDescriptor);
        TableAliasContext tableAliasContext = new BaseTableAliasContext(tableAlias, entityDescriptor.getTableName());
        if (joinOperator != null)
            queryContext.addFromContext(joinOperator, tableAliasContext);
        else
            queryContext.addFromContext(tableAliasContext);
        return tableAlias;
    }

    private String createEntityDescriptorContext(EntityDescriptor entityDescriptor) {
        return createEntityDescriptorContext(null, entityDescriptor);
    }

    private String registerAlias(EntityDescriptor descriptor) {
        long descriptorsCount = entityDescriptorsByAlias.values().stream().filter(d -> d == descriptor).count();
        String alias = descriptor.getTableName() + String.valueOf(descriptorsCount);
        entityDescriptorsByAlias.put(alias, descriptor);
        return alias;
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

    private void addSimpleJoinConditions(String tableAlias, EntityDescriptor relatedDescriptor,
                                         List<ColumnDescriptor> columnsRelatedToFilled) {
        ColumnDescriptor columnRelatedToFilled = columnsRelatedToFilled.get(0);
        columnDescriptorAliasMapping.put(columnRelatedToFilled, tableAlias);
        ConditionContext condition = new BaseConditionContext(ConditionOperator.EQUALS);
        condition.addFirstExpression(new BaseTableColumnContext(firstEntityDescriptorAlias, entityDescriptor.getTableName(),
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
        condition.addCompareExpression(new BaseTableColumnContext(tableAlias, relatedDescriptor.getTableName(),
                relatedColumn.getColumnName(), relatedColumn.getColumnType()));
        queryContext.getConditionsContext().addConditionContext(ConditionsOperator.AND, condition);
    }

    public EntityBasedQuery<T> fill(Class filledEntityClass) {
        // Safe checks
        EntityDescriptor relatedDescriptor = findEntityDescriptorWithClass(filledEntityClass);
        List<ColumnDescriptor> columnsRelatedToFilled = safeGetColumnsRelatedToFilled(filledEntityClass);
        // Context creation
        String tableAlias = createEntityDescriptorContext(JoinOperator.JOIN, relatedDescriptor);
        addSimpleJoinConditions(tableAlias, relatedDescriptor, columnsRelatedToFilled);
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
        ResultSetObjectMaker<T> objectMaker = new ResultSetObjectMaker<>(entityDescriptor,
                entityDescriptorsByAlias, columnDescriptorAliasMapping, rs);
        return objectMaker.make();
    }
}
