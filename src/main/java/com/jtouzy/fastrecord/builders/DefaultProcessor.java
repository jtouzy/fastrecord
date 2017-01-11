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

public abstract class DefaultProcessor<T,E extends WritableContext> implements Processor<T,E> {

    // =============================================================================
    // Attributes
    // =============================================================================

    private static final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);
    private final WriterCache writerCache;
    private final EntityPool entityPool;
    private final FastRecordConfiguration configuration;
    /**
     * Mapping between EntityDescriptors in the statement context and their alias
     */
    private final Map<String,EntityDescriptor> entityDescriptorsByAlias;
    private DataSource dataSource;
    private EntityDescriptor entityDescriptor;
    private E expression;

    // =============================================================================
    // Constructors
    // =============================================================================

    public DefaultProcessor(WriterCache writerCache, EntityPool entityPool, FastRecordConfiguration configuration) {
        this.writerCache = writerCache;
        this.entityPool = entityPool;
        this.configuration = configuration;
        entityDescriptorsByAlias = new LinkedHashMap<>();
    }

    // =============================================================================
    // Initialization overrides
    // =============================================================================

    @Override
    public void initProcessor(Class<T> entityClass) {
        entityDescriptor = findEntityDescriptorWithClass(entityClass);
        expression = createExpression();
    }

    // =============================================================================
    // Abstract methods
    // =============================================================================

    protected abstract E createExpression();

    // =============================================================================
    // Interface overrides
    // =============================================================================

    @Override
    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public E getExpression() {
        return expression;
    }

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    @Override
    public String getSql() {
        return writeMetadata().getSqlString().toString();
    }

    @Override
    public DbReadyStatementMetadata writeMetadata() {
        Writer<E> writer = writerCache.getWriter(expression);
        writer.write();
        return writer.getResult();
    }

    // =============================================================================
    // Protected methods
    // =============================================================================

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected FastRecordConfiguration getConfiguration() {
        return configuration;
    }

    protected Map<String, EntityDescriptor> getEntityDescriptorsByAlias() {
        return entityDescriptorsByAlias;
    }

    protected void printSql(DbReadyStatementMetadata metadata) {
        if (configuration.isPrintSql()) {
            logger.info("Execute SQL [{}], [{}]", metadata.getSqlString(), metadata.getParameters());
        }
    }

    protected String registerAlias(EntityDescriptor descriptor) {
        long descriptorsCount = entityDescriptorsByAlias.values().stream().filter(d -> d == descriptor).count();
        String alias = descriptor.getTableName() + String.valueOf(descriptorsCount);
        entityDescriptorsByAlias.put(alias, descriptor);
        return alias;
    }

    protected EntityDescriptor findEntityDescriptorWithClass(Class entityClass) {
        Optional<EntityDescriptor> entityDescriptorOptional =
                entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new EntityNotFoundException(entityClass);
        }
        return entityDescriptorOptional.get();
    }

    protected Optional<String> findEntityDescriptorAlias(EntityDescriptor descriptor) {
        return entityDescriptorsByAlias.entrySet().stream()
                .filter(e -> e.getValue().equals(descriptor)).map(Map.Entry::getKey).findFirst();
    }

    protected String getFirstEntityDescriptorAlias() {
        Optional<String> optionalAlias = findEntityDescriptorAlias(getEntityDescriptor());
        if (!optionalAlias.isPresent()) {
            // This must never appear
            throw new IllegalStateException("First EntityDescriptor does not have an alias!");
        }
        return optionalAlias.get();
    }
}
