package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.Writer;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Entity based statement generalized processor.
 * Creates the statement metadata and executes it.
 *
 * @param <T> Entity class
 * @param <E> WritableContext to be created
 */
public abstract class EntityBasedProcessor<T, E extends WritableContext> implements Processor {

    // ---------------------------------------------------------------------------------------------
    // Static final properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Logger instance.
     */
    private static final Logger logger = LoggerFactory.getLogger(EntityBasedProcessor.class);

    // ---------------------------------------------------------------------------------------------
    // Injected final properties
    // ---------------------------------------------------------------------------------------------

    /**
     * EntityPool instance.
     */
    private final EntityPool entityPool;

    /**
     * WriterCache for this QueryContext.
     */
    private final WriterCache writerCache;

    /**
     * FastRecord configuration.
     */
    private final FastRecordConfiguration configuration;

    // ---------------------------------------------------------------------------------------------
    // Metadata and tools properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Expression to be build
     */
    protected E expression;

    /**
     * EntityDescriptor of the base class in this statement.
     */
    private EntityDescriptor entityDescriptor;

    /**
     * DataSource used to get a database Connection.
     */
    private DataSource dataSource;

    // ---------------------------------------------------------------------------------------------
    // Work properties
    // ---------------------------------------------------------------------------------------------

    /**
     * Mapping between EntityDescriptors in the statement context and their alias
     */
    private final Map<String,EntityDescriptor> entityDescriptorsByAlias;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Constructor
     * @param entityPool EntityPool instance
     * @param writerCache WriterCache for this statement
     * @param configuration FastRecord global configuration
     */
    @Autowired
    public EntityBasedProcessor(EntityPool entityPool, WriterCache writerCache, FastRecordConfiguration configuration) {
        this.entityPool = entityPool;
        this.writerCache = writerCache;
        this.configuration = configuration;
        entityDescriptorsByAlias = new LinkedHashMap<>();
    }

    // ---------------------------------------------------------------------------------------------
    // General accessors
    // ---------------------------------------------------------------------------------------------

    /**
     * Setter of the DataSource used to get a database Connection.
     * This method is called by Autowire system but can be called manually.
     * @param dataSource DataSource to be set
     */
    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected EntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    protected Map<String, EntityDescriptor> getEntityDescriptorsByAlias() {
        return entityDescriptorsByAlias;
    }

    protected FastRecordConfiguration getConfiguration() {
        return configuration;
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Initialization method.
     * This method is called when Statement bean methods are called.
     *
     * @param entityClass The Entity class target of the statement
     */
    public void init(Class<T> entityClass) {
        initializeContext(entityClass);
    }

    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Context initialization.
     * Initialize the ConditionsConfigurer.
     */
    protected void initializeContext(Class<T> entityClass) {
        this.entityDescriptor = findEntityDescriptorWithClass(entityClass);
    }

    /**
     * Print SQL in logger if the configuration enables it.
     * The property to enable the SQL print is FastRecordConfiguration.PRINT_SQL
     *
     * @param metadata SQL metadata to print
     */
    protected void printSql(DbReadyStatementMetadata metadata) {
        if (configuration.isPrintSql()) {
            logger.info("Execute SQL [{}], [{}]", metadata.getSqlString(), metadata.getParameters());
        }
    }

    /**
     * Create the SQLMetadata based on the statement context.
     * All the writing process is called from this method.
     *
     * @return SQLMetadata created based on this statement
     */
    @Override
    public DbReadyStatementMetadata writeMetadata() {
        Writer<E> writer = writerCache.getWriter(expression);
        writer.write();
        return writer.getResult();
    }

    // ---------------------------------------------------------------------------------------------
    // Protected utility methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Register an alias on an EntityDescriptor.
     * This method checks if another same EntityDescriptor is already created in the query context
     * and increment a counter to create the EntityDescriptor alias.
     *
     * @param descriptor EntityDescriptor to create alias with
     * @return The newly created alias
     */
    protected String registerAlias(EntityDescriptor descriptor) {
        long descriptorsCount = entityDescriptorsByAlias.values().stream().filter(d -> d == descriptor).count();
        String alias = descriptor.getTableName() + String.valueOf(descriptorsCount);
        entityDescriptorsByAlias.put(alias, descriptor);
        return alias;
    }

    /**
     * Get the base EntityDescriptor alias
     *
     * @return First EntityDescriptor alias (base class of the Query)
     */
    protected String getFirstEntityDescriptorAlias() {
        Optional<String> optionalAlias = findEntityDescriptorAlias(getEntityDescriptor());
        if (!optionalAlias.isPresent()) {
            // This must never appear
            throw new IllegalStateException("First EntityDescriptor does not have an alias!");
        }
        return optionalAlias.get();
    }

    /**
     * Find and check an EntityDescriptor with a given class.
     * If no EntityDescriptor is related to the given class, an <code>EntityNotFoundException</code> will be thrown.
     *
     * @param entityClass Class for searching the EntityDescriptor
     * @return EntityDescriptor related to the given class
     */
    protected EntityDescriptor findEntityDescriptorWithClass(Class entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        return entityDescriptorOptional.get();
    }

    /**
     * Find the first alias of a given EntityDescriptor.
     * An EntityDescriptor may appear multiple times in the entityDescriptor mapping, so this
     * method is not really helpful in this case. But it can be helpful with simple queries.
     *
     * @param descriptor EntityDescriptor origin
     * @return Alias linked to the EntityDescriptor (optional), or Optional.empty()
     */
    protected Optional<String> findEntityDescriptorAlias(EntityDescriptor descriptor) {
        return entityDescriptorsByAlias.entrySet().stream()
                .filter(e -> e.getValue().equals(descriptor)).map(Map.Entry::getKey).findFirst();
    }
}
