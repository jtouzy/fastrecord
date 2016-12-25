package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityBasedQuery;
import com.jtouzy.fastrecord.builders.Query;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.entity.EntityPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> implements Repository<T>, InitializingBean {
    @Autowired
    private EntityPool entityPool;
    protected final Class<T> entityClass;
    protected EntityDescriptor entityDescriptor;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void afterPropertiesSet()
    throws Exception {
        Optional<EntityDescriptor> entityDescriptorOptional = entityPool.getEntityDescriptor(entityClass);
        if (!entityDescriptorOptional.isPresent()) {
            throw new RepositoryDefinitionException("Class [" + entityClass + "] is not an Entity");
        }
        this.entityDescriptor = entityDescriptorOptional.get();
    }

    @Override
    public List<T> findAll() {
        return improveQuery(Query.from(entityClass)).findAll();
    }

    protected EntityBasedQuery<T> improveQuery(EntityBasedQuery<T> query) {
        return query;
    }
}
