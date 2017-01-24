package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.QueryProcessor;
import com.jtouzy.fastrecord.builders.Statement;
import com.jtouzy.fastrecord.builders.StatementException;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.lifecycle.FastRecordInitializedEvent;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> implements Repository<T> {
    @Inject
    private EntityPool entityPool;
    @Inject
    protected Statement statementProcessor;

    protected final Class<T> entityClass;
    protected EntityDescriptor entityDescriptor;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @EventListener
    public void handleInitialized(FastRecordInitializedEvent event)
    throws Exception {
        Optional<EntityDescriptor> entityDescriptorOptional = entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new RepositoryDefinitionException("Class [" + entityClass + "] is not an Entity");
        }
        this.entityDescriptor = entityDescriptorOptional.get();
    }

    @Override
    public List<T> findAll() {
        return improveQuery(statementProcessor.queryFrom(entityClass)).findAll();
    }

    @Override
    public T create(T object) throws StatementException {
        statementProcessor.insert(object).execute();
        return object;
    }

    @Override
    public T update(T object) throws StatementException {
        statementProcessor.update(object).execute();
        return object;
    }

    @Override
    public void delete(T object) throws StatementException {
        statementProcessor.delete(object).execute();
    }

    protected QueryProcessor<T> improveQuery(QueryProcessor<T> query) {
        return query;
    }
}
