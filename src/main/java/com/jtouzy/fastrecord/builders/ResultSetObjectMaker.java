package com.jtouzy.fastrecord.builders;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultSetObjectMaker<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetObjectMaker.class);
    private final FastRecordConfiguration configuration;
    private final QueryExpression queryContext;
    private final ResultSet resultSet;
    private final Map<String,EntityDescriptor> entityDescriptorsByAlias;
    private final Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping;
    private final Map<String,Object> currentRowValues;
    private final Table<Object,String,Object> currentRowInstanceCache;
    private final Map<String,Object> currentRowAliasInstanceMapping;

    public ResultSetObjectMaker(FastRecordConfiguration configuration, QueryExpression queryContext,
                                Map<String,EntityDescriptor> entityDescriptorsByAlias,
                                Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping,
                                ResultSet resultSet) {
        this.configuration = configuration;
        this.queryContext = queryContext;
        this.entityDescriptorsByAlias = entityDescriptorsByAlias;
        this.columnDescriptorAliasMapping = columnDescriptorAliasMapping;
        this.resultSet = resultSet;
        currentRowValues = new HashMap<>();
        currentRowInstanceCache = HashBasedTable.create();
        currentRowAliasInstanceMapping = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public List<T> make() {
        List<T> results = new ArrayList<>();
        try {
            String columnLabel;
            logger.debug("Start creating result objects...");
            String mainTableAlias = queryContext.getMainTargetExpression().getAlias();
            while (resultSet.next()) {
                currentRowValues.clear();
                currentRowInstanceCache.clear();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i ++) {
                    // Access to the column label : tableAlias0$$tableColumn
                    columnLabel = resultSet.getMetaData().getColumnLabel(i);
                    // Stores the value in the value Map
                    currentRowValues.put(columnLabel, resultSet.getObject(columnLabel));
                }
                // Starts the object creation for the main object
                logger.debug("Start creating result object for row...");
                results.add((T)createObjectFromAlias(mainTableAlias));
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException ex) {
            // TODO IllegalArgumentException if argument type mismatch + Other exceptions?
            throw new ObjectCreationException(ex);
        }
        return results;
    }

    private Object createObjectFromAlias(String tableAlias)
    throws InvocationTargetException, IllegalAccessException, InstantiationException {
        EntityDescriptor entityDescriptor = entityDescriptorsByAlias.get(tableAlias);
        Object instance = currentRowAliasInstanceMapping.get(tableAlias);
        if (instance == null) {
            instance = newInstance(entityDescriptor.getClazz());
        }
        StringBuilder columnName = new StringBuilder();
        Object valueToSet;
        for (ColumnDescriptor columnDescriptor : entityDescriptor.getColumnDescriptors()) {
            columnName.delete(0, columnName.length());
            columnName.append(tableAlias)
                      .append(configuration.getColumnAliasSeparator())
                      .append(columnDescriptor.getColumnName());
            valueToSet = currentRowValues.get(columnName.toString());
            if (valueToSet != null) {
                setPropertyValue(tableAlias, instance, columnDescriptor, valueToSet);
            }
        }
        if (queryContext.getTargetJoinList().size() > 0) {
            List<String> joinedAliases = queryContext.getTargetJoinList().stream()
                    .filter(j -> j.getFirstTargetExpression().getAlias().equals(tableAlias))
                    .map(j -> j.getJoinTargetExpression().getAlias()).collect(Collectors.toList());
            for (String joinedAlias : joinedAliases) {
                createObjectFromAlias(joinedAlias);
            }
        }
        return instance;
    }

    private void setPropertyValue(String tableAlias, Object instance, ColumnDescriptor columnDescriptor, Object valueToSet)
    throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (columnDescriptor.isRelated()) {
            Object relatedInstance = currentRowInstanceCache.get(instance, columnDescriptor.getPropertyName());
            String alias = columnDescriptorAliasMapping.get(tableAlias, columnDescriptor);
            if (relatedInstance == null) {
                relatedInstance = newInstance(columnDescriptor.getPropertyType());
                currentRowInstanceCache.put(instance, columnDescriptor.getPropertyName(), relatedInstance);
                if (alias != null) {
                    currentRowAliasInstanceMapping.put(alias, relatedInstance);
                }
                invokeSetter(columnDescriptor, instance, relatedInstance);
            }
            setPropertyValue(alias == null ? tableAlias : alias, relatedInstance,
                    columnDescriptor.getRelatedColumn(), valueToSet);
        } else {
            invokeSetterWithConversion(columnDescriptor, instance, valueToSet);
        }
    }

    private Object newInstance(Class clazz)
    throws InstantiationException, IllegalAccessException {
        logger.debug("New instance of [{}]", clazz);
        return clazz.newInstance();
    }

    private void invokeSetterWithConversion(ColumnDescriptor columnDescriptor, Object instance, Object valueToSet)
    throws InvocationTargetException, IllegalAccessException {
        invokeSetter(columnDescriptor, instance, columnDescriptor.getTypeManager().convertToObject(valueToSet));
    }

    private void invokeSetter(ColumnDescriptor columnDescriptor, Object instance, Object valueToSet)
    throws InvocationTargetException, IllegalAccessException {
        logger.debug("Invoke on [{}], [{}] with [{}]", instance,
                columnDescriptor.getPropertySetter().getName(), valueToSet);
        columnDescriptor.getPropertySetter().invoke(instance, valueToSet);
    }
}
