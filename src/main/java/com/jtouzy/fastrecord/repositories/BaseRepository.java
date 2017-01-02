package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityQueryProcessor;
import com.jtouzy.fastrecord.builders.Statement;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.lifecycle.FastRecordInitializedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> implements Repository<T> {
    @Autowired
    private EntityPool entityPool;
    @Autowired
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

    protected EntityQueryProcessor<T> improveQuery(EntityQueryProcessor<T> query) {
        return query;
    }
}
