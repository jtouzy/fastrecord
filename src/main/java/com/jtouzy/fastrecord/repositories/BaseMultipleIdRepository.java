package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.EntityQueryProcessor;
import com.jtouzy.fastrecord.builders.QueryProcessor;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;

import java.util.Iterator;
import java.util.Optional;

public abstract class BaseMultipleIdRepository<T> extends BaseRepository<T> implements MultipleIdRepository<T> {
    public BaseMultipleIdRepository(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public Optional<T> findById(Object... ids) {
        QueryProcessor<T> query = improveQuery(statementProcessor.queryFrom(entityClass));
        int index = 0;
        ColumnDescriptor columnDescriptor;
        for (Iterator<ColumnDescriptor> it = entityDescriptor.getIdColumnDescriptors().iterator(); it.hasNext(); ++ index) {
            columnDescriptor = it.next();
            query.conditions().eq(columnDescriptor.getColumnName(), ids[index]);
        }
        return query.findFirst();
    }
}
