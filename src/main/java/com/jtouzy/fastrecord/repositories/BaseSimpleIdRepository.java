package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityQueryProcessor;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.lifecycle.FastRecordInitializedEvent;

import java.util.Optional;

public abstract class BaseSimpleIdRepository<T,ID> extends BaseRepository<T> implements SimpleIdRepository<T,ID> {
    public BaseSimpleIdRepository(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public void handleInitialized(FastRecordInitializedEvent event)
    throws Exception {
        super.handleInitialized(event);
        if (entityDescriptor.getIdColumnDescriptors().size() > 1) {
            throw new RepositoryDefinitionException("Entity with class [" + entityDescriptor.getClazz() +
                    "] have more than one ID column. You must use MultipleIdRepository.");
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityQueryProcessor<T> query = improveQuery(statementProcessor.queryFrom(entityClass));
        ColumnDescriptor descriptor = entityDescriptor.getIdColumnDescriptors().get(0);
        query.conditions().eq(descriptor.getColumnName(), id);
        return query.findFirst();
    }
}
