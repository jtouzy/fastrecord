package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConstants;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDefinitionException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.reflect.ResultSetObjectMaker;
import com.jtouzy.fastrecord.statements.context.BaseAliasTableColumnContext;
import com.jtouzy.fastrecord.statements.context.BaseConditionContext;
import com.jtouzy.fastrecord.statements.context.BaseJoinContext;
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
import java.util.Collections;
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
        firstEntityDescriptorAlias = registerAlias(entityDescriptor);
        queryContext = new BaseQueryContext(
                new BaseTableAliasContext(firstEntityDescriptorAlias, entityDescriptor.getTableName()));
        createEntityDescriptorContext(firstEntityDescriptorAlias, entityDescriptor);
    }

    private String createQueryColumnsWithEntity(String tableAlias, ColumnDescriptor columnToFillDescriptor, EntityDescriptor entityDescriptor) {
        String columnAlias;
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            if (columnToFillDescriptor != null && columnDescriptor.equals(columnToFillDescriptor.getRelatedColumn())) {
                continue;
            }
            columnAlias = tableAlias + FastRecordConstants.COLUMN_ALIAS_SEPARATOR + columnDescriptor.getColumnName();
            queryContext.addColumnContext(new BaseAliasTableColumnContext(columnAlias, tableAlias,
                    entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                    columnDescriptor.getColumnType()));
        }
        return tableAlias;
    }

    private void createEntityDescriptorContext(String tableAlias, ColumnDescriptor columnDescriptor, JoinOperator joinOperator, EntityDescriptor entityDescriptor) {
        createQueryColumnsWithEntity(tableAlias, columnDescriptor, entityDescriptor);
        TableAliasContext tableAliasContext = new BaseTableAliasContext(tableAlias, entityDescriptor.getTableName());
        if (joinOperator != null) {
            queryContext.getJoinListContext().addJoinContext(
                    new BaseJoinContext(queryContext.getJoinListContext().getMainTableContext(),
                            joinOperator, tableAliasContext));
        }
    }

    private void createEntityDescriptorContext(String tableAlias, EntityDescriptor entityDescriptor) {
        createEntityDescriptorContext(tableAlias, null, null, entityDescriptor);
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

    private List<ColumnDescriptor> safeGetColumnsRelatedToFilled(Class relatedClass, String propertyName) {
        List<ColumnDescriptor> columnsRelatedToFilled = entityDescriptor.getDistinctColumnDescriptorsWithType(relatedClass);
        if (columnsRelatedToFilled.size() == 0) {
            throw new EntityDefinitionException("Entity [" + entityDescriptor.getClazz() +
                    "] does not have relation to [" + relatedClass + "]");
        }
        if (propertyName != null) {
            Optional<ColumnDescriptor> columnDescriptorOptional = columnsRelatedToFilled.stream()
                    .filter(p -> propertyName.equals(p.getPropertyName())).findFirst();
            if (!columnDescriptorOptional.isPresent()) {
                throw new EntityDefinitionException("Property (with name [" + propertyName + "]) in entity [" +
                        entityDescriptor.getClazz() + "] is not from type [" + relatedClass + "]");
            }
            return Collections.singletonList(columnDescriptorOptional.get());
        }
        return columnsRelatedToFilled;
    }

    private void addSimpleJoinConditions(String tableAlias, EntityDescriptor relatedDescriptor,
                                         ColumnDescriptor columnRelatedToFilled) {
        columnDescriptorAliasMapping.put(columnRelatedToFilled, tableAlias);
        List<ColumnDescriptor> idColumns = relatedDescriptor.getIdColumnDescriptors();
        ColumnDescriptor associatedColumnDescriptor;
        ConditionContext condition;
        List<ColumnDescriptor> associatedColumnDescriptors;
        for (ColumnDescriptor relatedIdColumn : idColumns) {
            // TODO revision of this case
            associatedColumnDescriptors = entityDescriptor.getColumnDescriptorsRelatedWith(relatedIdColumn);
            if (associatedColumnDescriptors.size() > 1) {
                associatedColumnDescriptor = columnRelatedToFilled;
            } else {
                associatedColumnDescriptor = associatedColumnDescriptors.get(0);
            }
            condition = new BaseConditionContext(ConditionOperator.EQUALS);
            condition.addFirstExpression(new BaseTableColumnContext(firstEntityDescriptorAlias, entityDescriptor.getTableName(),
                    associatedColumnDescriptor.getColumnName(), associatedColumnDescriptor.getColumnType()));
            condition.addCompareExpression(new BaseTableColumnContext(tableAlias, relatedDescriptor.getTableName(),
                    relatedIdColumn.getColumnName(), relatedIdColumn.getColumnType()));
            queryContext.getConditionsContext().addConditionContext(ConditionsOperator.AND, condition);
        }
    }

    public EntityBasedQuery<T> fill(Class filledEntityClass) {
        return fill(filledEntityClass, null);
    }

    public EntityBasedQuery<T> fill(Class filledEntityClass, String propertyName) {
        // Safe checks
        EntityDescriptor relatedDescriptor = findEntityDescriptorWithClass(filledEntityClass);
        // Columns which represents properties to fill
        // In case of two same type property in one entity (example : two participants in one match)
        // This columns represents the two participants columns
        List<ColumnDescriptor> columnsRelatedToFilled = safeGetColumnsRelatedToFilled(filledEntityClass, propertyName);
        // Context creation
        String tableAlias;
        for (ColumnDescriptor columnDescriptor : columnsRelatedToFilled) {
            tableAlias = registerAlias(relatedDescriptor);
            createEntityDescriptorContext(tableAlias, columnDescriptor, JoinOperator.JOIN, relatedDescriptor);
            addSimpleJoinConditions(tableAlias, relatedDescriptor, columnDescriptor);
        }
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
        ResultSetObjectMaker<T> objectMaker = new ResultSetObjectMaker<>(queryContext, entityDescriptorsByAlias,
                columnDescriptorAliasMapping, rs);
        return objectMaker.make();
    }

    public Optional<T> findFirst() {
        List<T> results = findAll();
        if (results.size() == 0)
            return Optional.empty();
        return Optional.of(results.get(0));
    }
}
