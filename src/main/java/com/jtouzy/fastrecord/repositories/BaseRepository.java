package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityBasedQuery;
import com.jtouzy.fastrecord.builders.Query;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T,ID> implements Repository<T,ID> {
    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        return improveQuery(Query.from(entityClass)).findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        // TODO review ID clause
        return improveQuery(Query.from(entityClass)).findFirst();
    }

    protected EntityBasedQuery<T> improveQuery(EntityBasedQuery<T> query) {
        return query;
    }
}
