package com.jtouzy.fastrecord.reflect;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.EntityDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetObjectMaker<T> {
    private final EntityDescriptor entityDescriptor;
    private final ResultSet resultSet;
    private final Map<String,EntityDescriptor> entityDescriptorsByAlias;
    private final Map<ColumnDescriptor,String> columnDescriptorAliasMapping;

    public ResultSetObjectMaker(EntityDescriptor entityDescriptor,
                                Map<String,EntityDescriptor> entityDescriptorsByAlias,
                                Map<ColumnDescriptor,String> columnDescriptorAliasMapping, ResultSet resultSet) {
        this.entityDescriptor = entityDescriptor;
        this.entityDescriptorsByAlias = entityDescriptorsByAlias;
        this.columnDescriptorAliasMapping = columnDescriptorAliasMapping;
        this.resultSet = resultSet;
    }

    @SuppressWarnings("unchecked")
    public List<T> make() {
        List<T> results = new ArrayList<>();
        try {
            T instance;
            String columnLabel;
            EntityDescriptor currentDescriptor;
            ColumnDescriptor descriptor;
            Object relatedInstance;
            Map<String,Object> relatedObjects;
            String tableAlias;
            while (resultSet.next()) {
                // New instance of the main object
                instance = (T)entityDescriptor.getClazz().newInstance();
                // Related objects in the main object
                // > This map is used to optimize because when a sub-entity is created, the instance is not stored
                //   except in the parent object property. So, to omit a reflection GET call of the property, we
                //   store the related objects created with their unique table alias.
                relatedObjects = new HashMap<>();
                // Iterate over the columns of the resultSet
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i ++) {
                    // Access to the column label : tableAlias0_tableColumn
                    columnLabel = resultSet.getMetaData().getColumnLabel(i);
                    // Get the current table alias : tableAlias0
                    tableAlias = columnLabel.substring(0, columnLabel.indexOf("_"));
                    // Get the current EntityDescriptor from the EntityDescriptor array
                    // > All the EntityDescriptors used in the Query are stored in the entityDescriptorByAlias
                    currentDescriptor = entityDescriptorsByAlias.get(tableAlias);
                    // Get the current ColumnDescriptor from the current EntityDescriptor
                    // TODO optional checking to the descriptor column
                    descriptor = currentDescriptor.getColumnDescriptorByColumn(columnLabel.substring(columnLabel.indexOf("_")+1)).get();
                    // 1 - When the EntityDescriptor is the base-ED (the main from the Query request) and when the
                    //     Column is a related one (from another object), we creates the instance (because all of
                    //     the properties of this objects will be fetched in the next columns of the ResultSet)
                    if (currentDescriptor == entityDescriptor && descriptor.isRelated()) {
                        // Instantiation of the new related object
                        relatedInstance = descriptor.getPropertyType().newInstance();
                        // Invoke the main-object property setter to set the newly created object
                        descriptor.getPropertySetter().invoke(instance, relatedInstance);
                        // Store the relatedInstance in the "related cache" to eventually access later
                        relatedObjects.put(columnDescriptorAliasMapping.get(descriptor), relatedInstance);
                        // Call the property setter of the relatedColumn in the relatedInstance to set the value
                        descriptor.getRelatedColumn().getPropertySetter()
                                .invoke(relatedInstance, resultSet.getObject(columnLabel));
                    } else {
                        // 2 - When the EntityDescriptor is not the main object, we need to access the instance object
                        //     created in (1) to call the property setter.
                        if (currentDescriptor != entityDescriptor) {
                            relatedInstance = relatedObjects.get(tableAlias);
                            descriptor.getPropertySetter().invoke(relatedInstance, resultSet.getObject(columnLabel));
                        // 3 - When the EntityDescriptor is the main, we just need to call the property setter
                        } else {
                            descriptor.getPropertySetter().invoke(instance, resultSet.getObject(columnLabel));
                        }
                    }
                }
                results.add(instance);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException ex) {
            throw new ObjectCreationException(ex);
        }
        return results;
    }
}
