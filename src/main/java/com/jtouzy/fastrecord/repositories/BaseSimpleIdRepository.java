package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityBasedQuery;
import com.jtouzy.fastrecord.builders.Query;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;

import java.util.Optional;

public abstract class BaseSimpleIdRepository<T,ID> extends BaseRepository<T> implements SimpleIdRepository<T,ID> {
    public BaseSimpleIdRepository(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public void afterPropertiesSet()
    throws Exception {
        super.afterPropertiesSet();
        if (entityDescriptor.getIdColumnDescriptors().size() > 1) {
            throw new RepositoryDefinitionException("Entity with class [" + entityDescriptor.getClazz() +
                    "] have more than one ID column. You must use MultipleIdRepository.");
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityBasedQuery<T> query = improveQuery(Query.from(entityClass));
        ColumnDescriptor descriptor = entityDescriptor.getIdColumnDescriptors().get(0);
        query.conditions().eq(descriptor.getColumnName(), id);
        return query.findFirst();
    }
}
