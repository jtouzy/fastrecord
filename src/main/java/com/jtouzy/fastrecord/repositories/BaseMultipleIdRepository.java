package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityBasedQuery;
import com.jtouzy.fastrecord.builders.Query;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;

import java.util.Iterator;
import java.util.Optional;

public class BaseMultipleIdRepository<T> extends BaseRepository<T> implements MultipleIdRepository<T> {
    public BaseMultipleIdRepository(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Optional<T> findById(Object... ids) {
        EntityBasedQuery<T> query = improveQuery(Query.from(entityClass));
        int index = 0;
        ColumnDescriptor columnDescriptor;
        for (Iterator<ColumnDescriptor> it = entityDescriptor.getIdColumnDescriptors().iterator(); it.hasNext(); ++ index) {
            columnDescriptor = it.next();
            query.conditions().eq(columnDescriptor.getColumnName(), String.valueOf(ids[index]));
        }
        return query.findFirst();
    }
}
