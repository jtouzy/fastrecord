package com.jtouzy.fastrecord.builders;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.*;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manager for an SQL query based on an Entity.
 *
 * @param <T> Entity class
 */
@Service("FastRecord.Core.EntityQueryProcessor")
@Scope("prototype")
public class EntityQueryProcessor<T> extends EntityBasedConditionsProcessor<T, QueryExpression> {

    // ---------------------------------------------------------------------------------------------
    // Static final properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Logger instance.
     */
    private static final Logger logger = LoggerFactory.getLogger(EntityQueryProcessor.class);

    // ---------------------------------------------------------------------------------------------
    // Working properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Mapping between ColumnDescriptors which are related to another Entities filled in the query.
     * The table row is the origin table alias, the column is the ColumnDescriptor instance, and
     * the final key is the related EntityDescriptor's alias.
     */
    private final Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    public EntityQueryProcessor(EntityPool entityPool, WriterCache writerCache, FastRecordConfiguration configuration) {
        super(entityPool, writerCache, configuration);
        this.columnDescriptorAliasMapping = HashBasedTable.create();
    }

    // ---------------------------------------------------------------------------------------------
    // Public API : Joins
    // ---------------------------------------------------------------------------------------------

    /**
     * Fill the result with the given class.
     * Check if the given class is related to the base class of the query, and then creates a simple
     * join with the EntityDescriptor based to the given class.
     * The given class must be directly related to the base class, otherwise
     * use the <code>fillFrom</code> method.
     *
     * @param filledEntityClass Class related to the base class of the query
     * @return QueryAPI object
     */
    public EntityQueryProcessor<T> fill(Class filledEntityClass) {
        return fill(filledEntityClass, null);
    }

    /**
     * Fill the result with the given class.
     * Check if the given class is related to the base class of the query, and then creates a simple
     * join with the EntityDescriptor based to the given class.
     * The given class must be directly related to the base class, otherwise
     * use the <code>fillFrom</code> method.
     * The <code>propertyName</code> is used to limit the fill to only one property (in the case of
     * having 2 same classes directly related to the base class).
     *
     * @param filledEntityClass Class related to the base class of the query
     * @param propertyName Name of the property to get the related Entity from the base class
     * @return QueryAPI object
     */
    public EntityQueryProcessor<T> fill(Class filledEntityClass, String propertyName) {
        return fillFrom(getEntityDescriptor(), filledEntityClass, propertyName);
    }

    /**
     * Fill the origin class with the given class.
     * Check if the given class is related to the origin class, and then creates a simple
     * join with the EntityDescriptor based to the given class.
     * The given class must be directly related to the origin class.
     *
     * @param originEntityClass Class to be filled with the given second class
     * @param filledEntityClass Class related to the origin class
     * @return QueryAPI object
     */
    public EntityQueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass) {
        return fillFrom(originEntityClass, filledEntityClass, null);
    }

    /**
     * Fill the origin class with the given class.
     * Check if the given class is related to the origin class, and then creates a simple
     * join with the EntityDescriptor based to the given class.
     * The given class must be directly related to the origin class.
     * The <code>propertyName</code> is used to limit the fill to only one property (in the case of
     * having 2 same classes directly related to the origin class).
     *
     * @param originEntityClass Class to be filled with the given second class
     * @param filledEntityClass Class related to the origin class
     * @param propertyName Name of the property to get the related Entity from the origin class
     * @return QueryAPI object
     */
    public EntityQueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass, String propertyName) {
        return fillFrom(findEntityDescriptorWithClass(originEntityClass), filledEntityClass, propertyName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public QueryConditionsConfigurer conditions() {
        return (QueryConditionsConfigurer)conditionsConfigurer;
    }

    // ---------------------------------------------------------------------------------------------
    // Public API : Query order
    // ---------------------------------------------------------------------------------------------

    public EntityQueryProcessor<T> orderBy(String columnName) {
        return orderBy(getEntityDescriptor(), columnName);
    }

    public EntityQueryProcessor<T> orderBy(Class entityClass, String columnName) {
        return orderBy(findEntityDescriptorWithClass(entityClass), columnName);
    }

    // ---------------------------------------------------------------------------------------------
    // Public API : Result methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Get the full SQL text of this query.
     * The SQL text is created with joins and conditions.
     *
     * @return Full SQL text of this query
     */
    public String getSql() {
        return writeMetadata().getSqlString().toString();
    }

    /**
     * Find all the results for this query.
     * Build the SQLMetadata, execute the statement, and create all the objects for result.
     *
     * @return All the objects created with the SQL query result
     */
    public List<T> findAll() {
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
            try (ResultSet rs = preparedStatement.executeQuery()) {
                ResultSetObjectMaker<T> objectMaker = new ResultSetObjectMaker<>(getConfiguration(), expression,
                        getEntityDescriptorsByAlias(), columnDescriptorAliasMapping, rs);
                return objectMaker.make();
            }
        } catch (SQLException ex) {
            throw new QueryException(ex);
        }
    }

    /**
     * Find the first result for this query.
     * The <code>findAll</code> method is called, but only the first result is send as return value.
     * This method can be used when the query is supposed to return only one result (by id), or just
     * if you want to check if the result returns something (prefer a count() or a native "FIRST"
     * instruction for this last case).
     *
     * @return Only the first object created with the SQL query result
     */
    public Optional<T> findFirst() {
        List<T> results = findAll();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    // ---------------------------------------------------------------------------------------------
    // Private initialization methods
    // ---------------------------------------------------------------------------------------------

    /**
     * QueryAPI context initialization.
     * Initialize the ConditionsConfigurer, the EntityDescriptor/alias mapping and the
     * ColumnDescriptor/alias mapping for related entities.
     * Creates the default base QueryContext.
     */
    @Override
    protected void initializeContext() {
        String firstEntityDescriptorAlias = registerAlias(getEntityDescriptor());
        expression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        firstEntityDescriptorAlias,
                        new DefaultSimpleTableExpression(getEntityDescriptor().getTableName())));
        createEntityDescriptorContext(firstEntityDescriptorAlias, getEntityDescriptor(),
                null, null, null, null);
        conditionsConfigurer = new QueryConditionsConfigurer(expression.getConditionChain());
    }

    /**
     * Create an EntityDescriptor context in the query context.
     * The SELECT columns context and the FROM context are created.
     *
     * @param tableAlias Alias of the EntityDescriptor in the query
     * @param columnDescriptor Column if the EntityDescriptor is a filled one, null otherwise (base class)
     * @param joinOperator Join operator if the EntityDescriptor is a filled one, null otherwise (base class)
     * @param entityDescriptor EntityDescriptor to create context with
     */
    private void createEntityDescriptorContext(String tableAlias, EntityDescriptor entityDescriptor,
                                               String originAlias, EntityDescriptor originEntityDescriptor,
                                               ColumnDescriptor columnDescriptor, JoinOperator joinOperator) {
        createQueryColumnsWithEntity(tableAlias, columnDescriptor, entityDescriptor);
        if (joinOperator != null) {
            expression.getTargetJoinList().add(
                    new DefaultQueryTargetExpressionJoin(
                            originEntityDescriptor.equals(getEntityDescriptor()) ?
                                    expression.getMainTargetExpression() :
                                    new DefaultQueryTargetExpressionWrapper(
                                            originAlias,
                                            new DefaultSimpleTableExpression(originEntityDescriptor.getTableName())),
                            joinOperator,
                            new DefaultQueryTargetExpressionWrapper(
                                    tableAlias,
                                    new DefaultSimpleTableExpression(entityDescriptor.getTableName()))));
        }
    }

    /**
     * Create all the SELECT columns in the query context, based on an EntityDescriptor.
     * ALl the columns are created except the related ones (to avoid call the property setter multiple
     * times during object creation) : the information is present on the query only one time.
     *
     * @param tableAlias Alias of the EntityDescriptor in the query
     * @param columnToFillDescriptor Column if the EntityDescriptor is a filled one, null otherwise (base class)
     * @param entityDescriptor EntityDescriptor of all columns to be created
     */
    private void createQueryColumnsWithEntity(String tableAlias, ColumnDescriptor columnToFillDescriptor,
                                              EntityDescriptor entityDescriptor) {
        String columnAlias;
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            // Avoid to have multiple times the same info on the query columns
            if (columnToFillDescriptor != null && columnDescriptor.equals(columnToFillDescriptor.getRelatedColumn())) {
                continue;
            }
            columnAlias = tableAlias + getConfiguration().getColumnAliasSeparator() + columnDescriptor.getColumnName();
            expression.getColumns().add(
                    new DefaultQueryColumnExpressionWrapper(
                            columnAlias,
                            new DefaultAliasTableColumnExpression(
                                    columnDescriptor.getColumnType(),
                                    new DefaultAliasTableExpression(entityDescriptor.getTableName(), tableAlias),
                                    columnDescriptor.getColumnName())));
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Fill the origin class with the given class.
     * Check if the given class is related to the origin class, and then creates a simple
     * join with the EntityDescriptor based to the given class.
     * The given class must be directly related to the origin class.
     * The <code>propertyName</code> is used to limit the fill to only one property (in the case of
     * having 2 same classes directly related to the origin class).
     *
     * @param descriptor EntityDescriptor from the class to be filled with the given second class
     * @param filledEntityClass Class related to the origin class
     * @param propertyName Name of the property to get the related Entity from the origin class
     * @return QueryAPI object
     */
    private EntityQueryProcessor<T> fillFrom(EntityDescriptor descriptor, Class filledEntityClass, String propertyName) {
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
        return this;
    }

    /**
     * Get the columns of an EntityDescriptor related to a class.
     *
     * @param entityDescriptorOrigin Origin EntityDescriptor
     * @param relatedClass Related class in the EntityDescriptor
     * @param propertyName Name of the property to get the related Entity from the origin class, null if we
     *                     want to get all the related Entity (from all properties with this given class)
     * @return List of ColumnDescriptors related to the given class, from a base EntityDescriptor
     */
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

    /**
     * Add simple-join conditions based on 2 EntityDescriptors.
     * All the joins columns are created in the query context.
     *
     * @param originAlias Alias of the origin EntityDescriptor
     * @param entityDescriptorOrigin Origin EntityDescriptor
     * @param tableAlias Alias of the joined EntityDescriptor
     * @param relatedDescriptor Joined EntityDescriptor
     * @param columnRelatedToFilled Column in the origin class related to the filled Entity
     */
    private void addSimpleJoinConditions(String originAlias, EntityDescriptor entityDescriptorOrigin,
                                         String tableAlias, EntityDescriptor relatedDescriptor,
                                         ColumnDescriptor columnRelatedToFilled) {
        // Find the related Entity ID columns
        List<ColumnDescriptor> idColumns = relatedDescriptor.getIdColumnDescriptors();
        ColumnDescriptor associatedColumnDescriptor;
        QueryConditionChain conditionChain = new DefaultQueryConditionChain();
        ConditionsHelper.addCondition(expression.getConditionChain(), ConditionChainOperator.AND, conditionChain);
        QueryConditionChain condition;
        List<ColumnDescriptor> associatedColumnDescriptors;
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
            columnDescriptorAliasMapping.put(originAlias, associatedColumnDescriptor, tableAlias);
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

    private EntityQueryProcessor<T> orderBy(EntityDescriptor entityDescriptor, String columnName) {
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
        expression.getOrderByColumns().add(
                new DefaultAliasTableColumnExpression(
                        columnDescriptor.getColumnType(),
                        new DefaultAliasTableExpression(
                                entityDescriptor.getTableName(),
                                alias.get()),
                        columnDescriptor.getColumnName()));
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    // ConditionsConfigurer inheritance
    // ---------------------------------------------------------------------------------------------

    public class QueryConditionsConfigurer extends ConditionsConfigurer {
        private QueryConditionsConfigurer(ConditionChain parentConditionChain) {
            super(parentConditionChain);
        }

        @Override
        protected ConditionChain createDefaultConditionChain() {
            return new DefaultQueryConditionChain();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected ConditionChain createConditionWrapper(ColumnDescriptor columnDescriptor, ConditionOperator operator, Object value) {
            return new DefaultQueryConditionWrapper(
                    new DefaultAliasTableColumnExpression(
                            columnDescriptor.getColumnType(),
                            new DefaultAliasTableExpression(
                                    getProcessor().getEntityDescriptor().getTableName(),
                                    getProcessor().getFirstEntityDescriptorAlias()),
                            columnDescriptor.getColumnName()),
                    operator,
                    new DefaultConstantExpression(
                            columnDescriptor.getColumnType(),
                            columnDescriptor.getTypeManager().convertToDatabase(value)));
        }

        @Override
        protected EntityQueryProcessor<T> getProcessor() {
            return EntityQueryProcessor.this;
        }

        @Override
        public EntityQueryProcessor<T> endConditions() {
            return getProcessor();
        }

        // --------------------------------------------------------------------------------------------------------
        // Public conditions API delegates (only to return this instance)
        // --------------------------------------------------------------------------------------------------------

        @Override
        public QueryConditionsConfigurer eq(String columnName, Object value) {
            super.eq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer notEq(String columnName, Object value) {
            super.notEq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer like(String columnName, Object value) {
            super.like(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer notLike(String columnName, Object value) {
            super.notLike(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer andEq(String columnName, Object value) {
            super.andEq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer andNotEq(String columnName, Object value) {
            super.andNotEq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer andLike(String columnName, Object value) {
            super.andLike(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer andNotLike(String columnName, Object value) {
            super.andNotLike(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer orEq(String columnName, Object value) {
            super.orEq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer orNotEq(String columnName, Object value) {
            super.orNotEq(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer orLike(String columnName, Object value) {
            super.orLike(columnName, value);
            return this;
        }
        @Override
        public ConditionsConfigurer orNotLike(String columnName, Object value) {
            super.orNotLike(columnName, value);
            return this;
        }
    }
}
