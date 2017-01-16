package com.jtouzy.fastrecord.builders;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.entity.EntityDefinitionException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.AggregateFunctionType;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionWrapper;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAggregateFunctionExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionJoin;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.utils.Priority;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Process(value = QueryExpression.class, priority = Priority.NATIVE)
public class DefaultQueryProcessor<T>
        extends DefaultConditionsProcessor<T, QueryExpression> implements QueryProcessor<T> {

    // =============================================================================
    // Attributes
    // =============================================================================

    /**
     * Mapping between ColumnDescriptors which are related to another Entities filled in the query.
     * The table row is the origin table alias, the column is the ColumnDescriptor instance, and
     * the final key is the related EntityDescriptor's alias.
     */
    private final Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping;

    // =============================================================================
    // Constructors
    // =============================================================================

    @Inject
    public DefaultQueryProcessor(WriterCache writerCache, EntityPool entityPool,
                                 FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
        this.columnDescriptorAliasMapping = HashBasedTable.create();
    }

    // =============================================================================
    // Interface overrides
    // =============================================================================

    @Override
    public QueryProcessor<T> fill(Class filledEntityClass) {
        return fill(filledEntityClass, null);
    }

    @Override
    public QueryProcessor<T> fill(Class filledEntityClass, String propertyName) {
        fillFrom(getEntityDescriptor(), filledEntityClass, propertyName);
        return this;
    }

    @Override
    public QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass) {
        return fillFrom(originEntityClass, filledEntityClass, null);
    }

    @Override
    public QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass, String propertyName) {
        fillFrom(findEntityDescriptorWithClass(originEntityClass), filledEntityClass, propertyName);
        return this;
    }

    @Override
    public QueryProcessor<T> orderBy(String columnName) {
        orderBy(getEntityDescriptor(), columnName);
        return this;
    }

    @Override
    public QueryProcessor<T> orderBy(Class entityClass, String columnName) {
        orderBy(findEntityDescriptorWithClass(entityClass), columnName);
        return this;
    }

    @Override
    public Integer count() {
        return count(getEntityDescriptor().getIdColumnDescriptors().get(0));
    }

    @Override
    public Integer count(String columnName) {
        return count(safeGetColumnDescriptor(columnName));
    }

    @Override
    public Optional<T> findFirst() {
        List<T> results = findAll();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    @Override
    public List<T> findAll() {
        DbReadyStatementMetadata metadata = writeMetadata();
        String sqlString = metadata.getSqlString().toString();
        printSql(metadata);
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlString)) {
            appendParameters(metadata, preparedStatement);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                ResultSetObjectMaker<T> objectMaker = new ResultSetObjectMaker<>(getConfiguration(), getExpression(),
                        getEntityDescriptorsByAlias(), columnDescriptorAliasMapping, rs);
                return objectMaker.make();
            }
        } catch (SQLException ex) {
            throw new QueryException(ex);
        }
    }

    // =============================================================================
    // Abstract methods overrides
    // =============================================================================

    @Override
    protected QueryExpression createExpression() {
        String firstEntityDescriptorAlias = registerAlias(getEntityDescriptor());
        return new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        firstEntityDescriptorAlias,
                        new DefaultSimpleTableExpression(getEntityDescriptor().getTableName())));
    }

    @Override
    protected ConditionChain createDefaultConditionChain() {
        return new DefaultQueryConditionChain();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <C extends ConditionChain & ConditionWrapper> C createConditionWrapper(
            ColumnDescriptor columnDescriptor, ConditionOperator operator, Object value) {
        return (C)new DefaultQueryConditionWrapper(
                new DefaultAliasTableColumnExpression(
                        columnDescriptor.getColumnType(),
                        new DefaultAliasTableExpression(
                                getEntityDescriptor().getTableName(),
                                getFirstEntityDescriptorAlias()),
                        columnDescriptor.getColumnName()),
                operator,
                new DefaultConstantExpression(
                        columnDescriptor.getColumnType(),
                        columnDescriptor.getTypeManager().convertToDatabase(value)));
    }

    // =============================================================================
    // Super classes overrides
    // =============================================================================

    @Override
    public void initProcessor(Class<T> entityClass) {
        super.initProcessor(entityClass);
        // TODO safe check method to get alias (already present) - use like getFirstEntityDescAlias?
        String alias = findEntityDescriptorAlias(getEntityDescriptor()).get();
        createEntityDescriptorContext(alias, getEntityDescriptor(),
                null, null, null, null);
        initConditionsProcessor(getExpression().getConditionChain());
    }

    // =============================================================================
    // Interface conditions overrides
    // =============================================================================


    @Override
    public QueryProcessor<T> chain() {
        super.chain();
        return this;
    }

    @Override
    public QueryProcessor<T> and() {
        super.and();
        return this;
    }

    @Override
    public QueryProcessor<T> or() {
        super.or();
        return this;
    }

    @Override
    public QueryProcessor<T> end() {
        super.end();
        return this;
    }

    @Override
    public QueryProcessor<T> eq(String columnName, Object value) {
        super.eq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> notEq(String columnName, Object value) {
        super.notEq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> like(String columnName, Object value) {
        super.like(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> notLike(String columnName, Object value) {
        super.notLike(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> in(String columnName, List<?> values) {
        super.in(columnName, values);
        return this;
    }

    @Override
    public QueryProcessor<T> notIn(String columnName, List<?> values) {
        super.notIn(columnName, values);
        return this;
    }

    @Override
    public QueryProcessor<T> andEq(String columnName, Object value) {
        super.andEq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> andNotEq(String columnName, Object value) {
        super.andNotEq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> andLike(String columnName, Object value) {
        super.andLike(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> andNotLike(String columnName, Object value) {
        super.andNotLike(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> andIn(String columnName, List<?> values) {
        super.andIn(columnName, values);
        return this;
    }

    @Override
    public QueryProcessor<T> andNotIn(String columnName, List<?> values) {
        super.andNotIn(columnName, values);
        return this;
    }

    @Override
    public QueryProcessor<T> orEq(String columnName, Object value) {
        super.orEq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> orNotEq(String columnName, Object value) {
        super.orNotEq(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> orLike(String columnName, Object value) {
        super.orLike(columnName, value);
        return this;
    }

    @Override
    public QueryProcessor<T> orNotLike(String columnName, Object value) {
        super.orNotLike(columnName, value);
        return this;
    }

    @Override
    public  QueryProcessor<T> orIn(String columnName, List<?> values) {
        super.orIn(columnName, values);
        return this;
    }

    @Override
    public  QueryProcessor<T> orNotIn(String columnName, List<?> values) {
        super.orNotIn(columnName, values);
        return this;
    }

    // =============================================================================
    // Private methods
    // =============================================================================

    private void createEntityDescriptorContext(String tableAlias, EntityDescriptor entityDescriptor,
                                               String originAlias, EntityDescriptor originEntityDescriptor,
                                               ColumnDescriptor columnDescriptor, JoinOperator joinOperator) {
        createQueryColumnsWithEntity(tableAlias, columnDescriptor, entityDescriptor);
        if (joinOperator != null) {
            getExpression().getTargetJoinList().add(
                    new DefaultQueryTargetExpressionJoin(
                            originEntityDescriptor.equals(getEntityDescriptor()) ?
                                    getExpression().getMainTargetExpression() :
                                    new DefaultQueryTargetExpressionWrapper(
                                            originAlias,
                                            new DefaultSimpleTableExpression(originEntityDescriptor.getTableName())),
                            joinOperator,
                            new DefaultQueryTargetExpressionWrapper(
                                    tableAlias,
                                    new DefaultSimpleTableExpression(entityDescriptor.getTableName()))));
        }
    }

    private void createQueryColumnsWithEntity(String tableAlias, ColumnDescriptor columnToFillDescriptor,
                                              EntityDescriptor entityDescriptor) {
        String columnAlias;
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            // Avoid to have multiple times the same info on the query columns
            if (columnToFillDescriptor != null && columnDescriptor.equals(columnToFillDescriptor.getRelatedColumn())) {
                continue;
            }
            columnAlias = tableAlias + getConfiguration().getColumnAliasSeparator() + columnDescriptor.getColumnName();
            getExpression().getColumns().add(
                    new DefaultQueryColumnExpressionWrapper(
                            columnAlias,
                            new DefaultAliasTableColumnExpression(
                                    columnDescriptor.getColumnType(),
                                    new DefaultAliasTableExpression(entityDescriptor.getTableName(), tableAlias),
                                    columnDescriptor.getColumnName())));
        }
    }

    private void fillFrom(EntityDescriptor descriptor, Class filledEntityClass, String propertyName) {
        // Safe checks
        EntityDescriptor relatedDescriptor = findEntityDescriptorWithClass(filledEntityClass);
        // Columns which represents properties to fill
        // In case of two same type property in one entity (example : two participants in one match)
        // This columns represents the two participants columns
        List<ColumnDescriptor> columnsRelatedToFilled =
                safeGetColumnsRelatedToFilled(descriptor, filledEntityClass, propertyName);
        // Get the origin alias
        // TODO improve this to pass custom alias to fill() method to avoid duplicate EntityDescriptor
        Optional<String> originAliasOptional = findEntityDescriptorAlias(descriptor);
        if (!originAliasOptional.isPresent()) {
            throw new IllegalStateException("Entity with class [" + descriptor.getClazz() +
                    "] is not registered in this query, or is not filled");
        }
        // Context creation
        String tableAlias;
        String originAlias = originAliasOptional.get();
        for (ColumnDescriptor columnDescriptor : columnsRelatedToFilled) {
            tableAlias = registerAlias(relatedDescriptor);
            createEntityDescriptorContext(tableAlias, relatedDescriptor,
                    originAlias, descriptor, columnDescriptor, JoinOperator.JOIN);
            addSimpleJoinConditions(originAlias, descriptor, tableAlias, relatedDescriptor, columnDescriptor);
        }
    }

    private List<ColumnDescriptor> safeGetColumnsRelatedToFilled(EntityDescriptor entityDescriptorOrigin,
                                                                 Class relatedClass, String propertyName) {
        List<ColumnDescriptor> columnsRelatedToFilled =
                entityDescriptorOrigin.getDistinctColumnDescriptorsWithType(relatedClass);
        if (columnsRelatedToFilled.isEmpty()) {
            throw new EntityDefinitionException("Entity [" + entityDescriptorOrigin.getClazz() +
                    "] does not have relation to [" + relatedClass + "]");
        }
        if (propertyName != null) {
            Optional<ColumnDescriptor> columnDescriptorOptional = columnsRelatedToFilled.stream()
                    .filter(p -> propertyName.equals(p.getPropertyName())).findFirst();
            if (!columnDescriptorOptional.isPresent()) {
                throw new EntityDefinitionException("Property (with name [" + propertyName + "]) in entity [" +
                        entityDescriptorOrigin.getClazz() + "] is not from type [" + relatedClass + "]");
            }
            return Collections.singletonList(columnDescriptorOptional.get());
        }
        return columnsRelatedToFilled;
    }

    private void addSimpleJoinConditions(String originAlias, EntityDescriptor entityDescriptorOrigin,
                                         String tableAlias, EntityDescriptor relatedDescriptor,
                                         ColumnDescriptor columnRelatedToFilled) {
        // Find the related Entity ID columns
        List<ColumnDescriptor> idColumns = relatedDescriptor.getIdColumnDescriptors();
        ColumnDescriptor associatedColumnDescriptor;
        QueryConditionChain conditionChain = new DefaultQueryConditionChain();
        ConditionsHelper.addCondition(getExpression().getConditionChain(), ConditionChainOperator.AND, conditionChain);
        QueryConditionChain condition;
        List<ColumnDescriptor> associatedColumnDescriptors;
        columnDescriptorAliasMapping.put(originAlias, columnRelatedToFilled, tableAlias);
        // Iterate over the related Entity ID columns
        for (ColumnDescriptor relatedIdColumn : idColumns) {
            // Get the origin columns related to the destination ID column
            associatedColumnDescriptors = entityDescriptorOrigin.getColumnDescriptorsRelatedWith(relatedIdColumn);
            if (associatedColumnDescriptors.isEmpty()) {
                // In the case of an Entity is related to himself, they may don't have all the related columns
                // registered because the ID could be associated to another column.
                // So by default we take the same column (table is the same, but different alias)
                associatedColumnDescriptor = relatedIdColumn;
            } else if (associatedColumnDescriptors.size() > 1) {
                // In the case of an Entity relates 2 times the same Entity (example : Match with 2 Participant),
                // we need to take only the column we process (in method parameter), because this method is called
                // on a loop over all the related columns (first participant and second)
                associatedColumnDescriptor = columnRelatedToFilled;
            } else {
                // By default, take only the first column related to this ID column
                associatedColumnDescriptor = associatedColumnDescriptors.get(0);
            }
            // Condition creation
            condition = new DefaultQueryConditionWrapper(
                    new DefaultAliasTableColumnExpression(
                            associatedColumnDescriptor.getColumnType(),
                            new DefaultAliasTableExpression(entityDescriptorOrigin.getTableName(), originAlias),
                            associatedColumnDescriptor.getColumnName()),
                    ConditionOperator.EQUALS,
                    new DefaultAliasTableColumnExpression(
                            relatedIdColumn.getColumnType(),
                            new DefaultAliasTableExpression(relatedDescriptor.getTableName(), tableAlias),
                            relatedIdColumn.getColumnName()));
            ConditionsHelper.addCondition(conditionChain, ConditionChainOperator.AND, condition);
        }
    }

    private void orderBy(EntityDescriptor entityDescriptor, String columnName) {
        Optional<ColumnDescriptor> columnDescriptorOptional = entityDescriptor.getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, entityDescriptor.getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        // TODO when multiple EntityDescriptor, throw error / adapt method to send alias
        Optional<String> alias = findEntityDescriptorAlias(entityDescriptor);
        if (!alias.isPresent()) {
            // This error will never happen because the entityDescriptor is already checked
            throw new IllegalStateException("An alias must be present to identify the EntityDescriptor");
        }
        getExpression().getOrderByColumns().add(
                new DefaultAliasTableColumnExpression(
                        columnDescriptor.getColumnType(),
                        new DefaultAliasTableExpression(
                                entityDescriptor.getTableName(),
                                alias.get()),
                        columnDescriptor.getColumnName()));
    }

    private void appendParameters(DbReadyStatementMetadata metadata, PreparedStatement preparedStatement)
    throws SQLException {
        int index = 1;
        for (DbReadyStatementParameter parameter : metadata.getParameters()) {
            preparedStatement.setObject(index, parameter.getValue(), parameter.getType());
            index ++;
        }
    }

    private Integer count(ColumnDescriptor columnDescriptor) {
        getExpression().getColumns().clear();
        getExpression().getColumns().add(
                new DefaultQueryColumnExpressionWrapper(
                        null,
                        new DefaultAggregateFunctionExpression(
                                AggregateFunctionType.COUNT,
                                new DefaultAliasTableColumnExpression(
                                        columnDescriptor.getColumnType(),
                                        new DefaultAliasTableExpression(
                                                getEntityDescriptor().getTableName(),
                                                getFirstEntityDescriptorAlias()),
                                        columnDescriptor.getColumnName()))));
        DbReadyStatementMetadata metadata = writeMetadata();
        String sqlString = metadata.getSqlString().toString();
        printSql(metadata);
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlString)) {
            appendParameters(metadata, preparedStatement);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new QueryException(ex);
        }
    }
}
