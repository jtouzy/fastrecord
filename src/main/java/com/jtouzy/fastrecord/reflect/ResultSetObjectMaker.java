package com.jtouzy.fastrecord.reflect;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jtouzy.fastrecord.config.FastRecordConstants;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDescriptor;
import com.jtouzy.fastrecord.statements.context2.QueryExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetObjectMaker<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetObjectMaker.class);
    private final QueryExpression queryContext;
    private final ResultSet resultSet;
    private final Map<String,EntityDescriptor> entityDescriptorsByAlias;
    private final Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping;

    public ResultSetObjectMaker(QueryExpression queryContext, Map<String,EntityDescriptor> entityDescriptorsByAlias,
                                Table<String,ColumnDescriptor,String> columnDescriptorAliasMapping, ResultSet resultSet) {
        this.queryContext = queryContext;
        this.entityDescriptorsByAlias = entityDescriptorsByAlias;
        this.columnDescriptorAliasMapping = columnDescriptorAliasMapping;
        this.resultSet = resultSet;
    }

    @SuppressWarnings("unchecked")
    public List<T> make() {
        List<T> results = new ArrayList<>();
        try {
            String columnLabel;
            Map<String,Object> rowValuesMap;

            logger.debug("Start creating result objects...");
            String mainTableAlias = queryContext.getMainTargetExpression().getAlias();
            while (resultSet.next()) {
                // Stores all the ResultSet row values in a HashMap to avoid multiple
                // loops over the resultSet columns during the object creation
                rowValuesMap = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i ++) {
                    // Access to the column label : tableAlias0$$tableColumn
                    columnLabel = resultSet.getMetaData().getColumnLabel(i);
                    // Stores the value in the value Map
                    rowValuesMap.put(columnLabel, resultSet.getObject(columnLabel));
                }
                // Starts the object creation for the main object
                logger.debug("Start creating result object for row...");
                results.add((T)createObjectFromResultSetRow(mainTableAlias, rowValuesMap));
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException ex) {
            // TODO IllegalArgumentException if argument type mismatch + Other exceptions?
            throw new ObjectCreationException(ex);
        }
        return results;
    }

    private Object createObjectFromResultSetRow(String tableAlias, Map<String,Object> values)
    throws InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        logger.debug("Start creating object with table alias [{}]", tableAlias);
        EntityDescriptor entityDescriptor = entityDescriptorsByAlias.get(tableAlias);
        Object instance = createObjectFromClass(entityDescriptor.getClazz());
        Map<String,Object> resultSetValuesForEntity = new LinkedHashMap<>();
        values.entrySet().stream().filter(e -> e.getKey().startsWith(tableAlias)).forEach(e ->
            resultSetValuesForEntity.put(
                    e.getKey().substring(e.getKey().indexOf(FastRecordConstants.COLUMN_ALIAS_SEPARATOR) +
                            FastRecordConstants.COLUMN_ALIAS_SEPARATOR.length()),
                    e.getValue())
        );
        logger.debug("Values to set on the new object [{}]", resultSetValuesForEntity);
        ColumnDescriptor columnDescriptor;
        String relatedAlias;
        Table<Object,String,Object> instanceCache = HashBasedTable.create();
        for (Map.Entry<String,Object> valueEntry : resultSetValuesForEntity.entrySet()) {
            // TODO safe checking of optional columnDescriptor
            columnDescriptor = entityDescriptor.getColumnDescriptorByColumn(valueEntry.getKey()).get();
            relatedAlias = columnDescriptorAliasMapping.get(tableAlias, columnDescriptor);
            invokeSetter(relatedAlias, valueEntry, values, instanceCache, instance, columnDescriptor, valueEntry.getValue());
        }
        return instance;
    }

    private Object createObjectFromClass(Class fromClass)
    throws InstantiationException, IllegalAccessException {
        return fromClass.newInstance();
    }

    private void invokeSetter(String tableAlias, Map.Entry<String,Object> valueEntry, Map<String,Object> values, Table<Object,String,Object> instanceCache, Object instance, ColumnDescriptor columnDescriptor, Object value)
    throws SQLException, InstantiationException, InvocationTargetException, IllegalAccessException {
        if (columnDescriptor.isRelated()) {
            Object relatedInstance;
            relatedInstance = instanceCache.get(instance, columnDescriptor.getPropertyName());
            if (relatedInstance == null) {
                if (tableAlias == null) {
                    relatedInstance = createObjectFromClass(columnDescriptor.getPropertyType());
                } else {
                    relatedInstance = createObjectFromResultSetRow(tableAlias, values);
                }
                logger.debug("Invoke setter on [{}] (property [{}]) with value [{}]", instance, columnDescriptor.getPropertyName(), relatedInstance);
                columnDescriptor.getPropertySetter().invoke(instance, relatedInstance);
                invokeSetter(tableAlias, valueEntry, values, instanceCache, relatedInstance, columnDescriptor.getRelatedColumn(), valueEntry.getValue());
                instanceCache.put(instance, columnDescriptor.getPropertyName(), relatedInstance);
            } else {
                invokeSetter(tableAlias, valueEntry, values, instanceCache, relatedInstance, columnDescriptor.getRelatedColumn(), valueEntry.getValue());
            }
        } else {
            logger.debug("Invoke setter on [{}] (property [{}]) with value [{}]", instance, columnDescriptor.getPropertyName(), value);
            columnDescriptor.getPropertySetter().invoke(instance, columnDescriptor.getTypeManager().convertToObject(value));
        }
    }
}
