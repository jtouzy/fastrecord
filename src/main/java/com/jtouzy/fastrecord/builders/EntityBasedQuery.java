package com.jtouzy.fastrecord.builders;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.config.FastRecordConstants;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDefinitionException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.reflect.ResultSetObjectMaker;
import com.jtouzy.fastrecord.statements.context2.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.JoinOperator;
import com.jtouzy.fastrecord.statements.context2.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context2.QueryExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryConditionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryTargetExpressionJoin;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.writers2.Writer;
import com.jtouzy.fastrecord.statements.writers2.WriterCache;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manager for an SQL query based on an Entity.
 *
 * @param <T> Entity class
 */
@Service
@Scope("prototype")
public class EntityBasedQuery<T> {
    /**
     * Logger instance.
     */
    private static final Logger logger = LoggerFactory.getLogger(EntityBasedQuery.class);

    // ---------------------------------------------------------------------------------------------
    // Injected properties
    // ---------------------------------------------------------------------------------------------

    /**
     * EntityPool instance.
     */
    @Autowired
    private EntityPool entityPool;
    /**
     * WriterCache for this QueryContext.
     */
    @Autowired
    private WriterCache writerCache;
    /**
     * DataSource used to get a database Connection.
     */
    @Autowired
    private DataSource dataSource;
    /**
     * FastRecord configuration.
     */
    @Autowired
    private FastRecordConfiguration configuration;

    // ---------------------------------------------------------------------------------------------
    // Metadata and tools properties
    // ---------------------------------------------------------------------------------------------

    /**
     * EntityDescriptor of the base class in this query.
     */
    EntityDescriptor entityDescriptor;
    /**
     * Global QueryContext of this query.
     */
    QueryExpression queryContext;
    /**
     * Conditions API Helper to build query conditions.
     */
    private ConditionsConfigurer<T> conditionsConfigurer;

    // ---------------------------------------------------------------------------------------------
    // Working properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Mapping between EntityDescriptors in the query context and their alias
     */
    private Map<String,EntityDescriptor> entityDescriptorsByAlias;
    /**
     * Mapping between ColumnDescriptors which are related to another Entities filled in the query.
     * The table row is the origin table alias, the column is the ColumnDescriptor instance, and
     * the final key is the related EntityDescriptor's alias.
     */
    private Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Private constructor.
     * The only method to instantiate the EntityBasedQuery is to use Query.from() static method.
     */
    private EntityBasedQuery() {
    }

    // ---------------------------------------------------------------------------------------------
    // Protected (package used)
    // ---------------------------------------------------------------------------------------------

    /**
     * Initialization method.
     * This method is called when Query.from() is called.
     *
     * @param entityClass The Entity class target of the query
     */
    void fromClass(Class<T> entityClass) {
        this.entityDescriptor = findEntityDescriptorWithClass(entityClass);
        initializeContext();
    }

    /**
     * Get the base EntityDescriptor alias
     *
     * @return First EntityDescriptor alias (base class of the Query)
     */
    String getFirstEntityDescriptorAlias() {
        Optional<String> optionalAlias = findEntityDescriptorAlias(entityDescriptor);
        if (!optionalAlias.isPresent()) {
            // This must never appear
            throw new IllegalStateException("First EntityDescriptor does not have an alias!");
        }
        return optionalAlias.get();
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
    public EntityBasedQuery<T> fill(Class filledEntityClass) {
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
    public EntityBasedQuery<T> fill(Class filledEntityClass, String propertyName) {
        return fillFrom(entityDescriptor, filledEntityClass, propertyName);
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
    public EntityBasedQuery<T> fillFrom(Class originEntityClass, Class filledEntityClass) {
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
    public EntityBasedQuery<T> fillFrom(Class originEntityClass, Class filledEntityClass, String propertyName) {
        return fillFrom(findEntityDescriptorWithClass(originEntityClass), filledEntityClass, propertyName);
    }

    // ---------------------------------------------------------------------------------------------
    // Public API : Conditions
    // ---------------------------------------------------------------------------------------------

    /**
     * Getter to access the Public Conditions API to add conditions to the request.
     * To get back to the Query API, use <code>end()</code> method on the Conditions API.
     *
     * @return The ConditionsConfigurer to manage conditions on the query
     */
    public ConditionsConfigurer<T> conditions() {
        return conditionsConfigurer;
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
        ResultSet rs;
        try {
            Connection connection = dataSource.getConnection();
            DbReadyStatementMetadata metadata = writeMetadata();
            String sqlString = metadata.getSqlString().toString();
            printSql(metadata);
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
        if (results.size() == 0)
            return Optional.empty();
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
    private void initializeContext() {
        this.conditionsConfigurer = new ConditionsConfigurer<>(this);
        this.entityDescriptorsByAlias = new LinkedHashMap<>();
        this.columnDescriptorAliasMapping = HashBasedTable.create();
        createQueryContextWithEntity();
    }

    /**
     * Create the base QueryContext of this query.
     * Create the first alias for the base EntityDescriptor, the first SELECT columns and FROM context.
     */
    private void createQueryContextWithEntity() {
        String firstEntityDescriptorAlias = registerAlias(entityDescriptor);
        queryContext = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        firstEntityDescriptorAlias,
                        new DefaultSimpleTableExpression(entityDescriptor.getTableName())));
        createBaseEntityDescriptorContext(firstEntityDescriptorAlias);
    }

    /**
     * Create an EntityDescriptor context (of the base class) in the query context.
     * The SELECT columns context and the FROM context are created.
     *
     * @param tableAlias Alias of the base EntityDescriptor in the query
     */
    private void createBaseEntityDescriptorContext(String tableAlias) {
        createEntityDescriptorContext(tableAlias, null, null, entityDescriptor);
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
    private void createEntityDescriptorContext(String tableAlias, ColumnDescriptor columnDescriptor,
                                               JoinOperator joinOperator, EntityDescriptor entityDescriptor) {
        createQueryColumnsWithEntity(tableAlias, columnDescriptor, entityDescriptor);
        if (joinOperator != null) {
            queryContext.getTargetJoinList().add(
                    new DefaultQueryTargetExpressionJoin(
                            queryContext.getMainTargetExpression(),
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
            columnAlias = tableAlias + FastRecordConstants.COLUMN_ALIAS_SEPARATOR + columnDescriptor.getColumnName();
            queryContext.getColumns().add(
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
    private EntityBasedQuery<T> fillFrom(EntityDescriptor descriptor, Class filledEntityClass, String propertyName) {
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
            createEntityDescriptorContext(tableAlias, columnDescriptor, JoinOperator.JOIN, relatedDescriptor);
            addSimpleJoinConditions(originAlias, descriptor, tableAlias, relatedDescriptor, columnDescriptor);
        }
        return this;
    }

    /**
     * Register an alias on an EntityDescriptor.
     * This method checks if another same EntityDescriptor is already created in the query context
     * and increment a counter to create the EntityDescriptor alias.
     *
     * @param descriptor EntityDescriptor to create alias with
     * @return The newly created alias
     */
    private String registerAlias(EntityDescriptor descriptor) {
        long descriptorsCount = entityDescriptorsByAlias.values().stream().filter(d -> d == descriptor).count();
        String alias = descriptor.getTableName() + String.valueOf(descriptorsCount);
        entityDescriptorsByAlias.put(alias, descriptor);
        return alias;
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
        if (columnsRelatedToFilled.size() == 0) {
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
        if (queryContext.getConditionChain().getChain().size() == 0) {
            queryContext.getConditionChain().addCondition(conditionChain);
        } else {
            queryContext.getConditionChain().addCondition(ConditionChainOperator.AND, conditionChain);
        }
        QueryConditionChain condition;
        List<ColumnDescriptor> associatedColumnDescriptors;
        // Iterate over the related Entity ID columns
        for (ColumnDescriptor relatedIdColumn : idColumns) {
            // Get the origin columns related to the destination ID column
            associatedColumnDescriptors = entityDescriptorOrigin.getColumnDescriptorsRelatedWith(relatedIdColumn);
            if (associatedColumnDescriptors.size() == 0) {
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
            conditionChain.addCondition(condition);
        }
    }

    /**
     * Print SQL in logger if the configuration enables it.
     * The property to enable the SQL print is FastRecordConfiguration.PRINT_SQL
     *
     * @param metadata SQL metadata to print
     */
    private void printSql(DbReadyStatementMetadata metadata) {
        if (configuration.isPrintSql()) {
            logger.info("Execute SQL [{}], [{}]", metadata.getSqlString(), metadata.getParameters());
        }
    }

    /**
     * Create the SQLMetadata based on the query context.
     * All the writing process is called from this method.
     *
     * @return SQLMetadata created based on this query
     */
    private DbReadyStatementMetadata writeMetadata() {
        Writer<QueryExpression> writer = writerCache.getWriter(queryContext);
        writer.write();
        return writer.getResult();
    }

    // ---------------------------------------------------------------------------------------------
    // Private utility methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Find the first alias of a given EntityDescriptor.
     * An EntityDescriptor may appear multiple times in the entityDescriptor mapping, so this
     * method is not really helpful in this case. But it can be helpful with simple queries.
     *
     * @param descriptor EntityDescriptor origin
     * @return Alias linked to the EntityDescriptor (optional), or Optional.empty()
     */
    private Optional<String> findEntityDescriptorAlias(EntityDescriptor descriptor) {
        return entityDescriptorsByAlias.entrySet().stream()
                .filter(e -> e.getValue().equals(descriptor)).map(Map.Entry::getKey).findFirst();
    }

    /**
     * Find and check an EntityDescriptor with a given class.
     * If no EntityDescriptor is related to the given class, an <code>EntityNotFoundException</code> will be thrown.
     *
     * @param entityClass Class for searching the EntityDescriptor
     * @return EntityDescriptor related to the given class
     */
    private EntityDescriptor findEntityDescriptorWithClass(Class entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        return entityDescriptorOptional.get();
    }
}
