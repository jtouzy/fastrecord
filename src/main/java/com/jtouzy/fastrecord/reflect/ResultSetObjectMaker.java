package com.jtouzy.fastrecord.reflect;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.statements.context.QueryContext;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetObjectMaker<T> {
    private final QueryContext queryContext;
    private final EntityDescriptor entityDescriptor;
    private final ResultSet resultSet;

    public ResultSetObjectMaker(QueryContext queryContext, EntityDescriptor entityDescriptor, ResultSet resultSet) {
        this.queryContext = queryContext;
        this.entityDescriptor = entityDescriptor;
        this.resultSet = resultSet;
    }

    @SuppressWarnings("unchecked")
    public List<T> make() {
        List<T> results = new ArrayList<>();
        try {
            T instance;
            while (resultSet.next()) {
                // TODO iterate over From Context on Each Table
                // TODO discover for each From Table Alias the EntityDescriptor mapped (need to get it from Query)
                instance = (T)entityDescriptor.getClazz().newInstance();
                for (ColumnDescriptor descriptor : entityDescriptor.getColumnDescriptors()) {
                    descriptor.getPropertySetter().invoke(instance, resultSet.getObject(new StringBuilder().append(entityDescriptor.getTableName())
                            .append(".").append(descriptor.getColumnName()).toString()));
                }
                results.add(instance);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException ex) {
            throw new ObjectCreationException(ex);
        }
        return results;
    }
}
