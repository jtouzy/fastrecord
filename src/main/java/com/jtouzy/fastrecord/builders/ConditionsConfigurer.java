package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConditionsConfigurer<T> {
    private final EntityBasedQuery<T> queryBuilder;
    private final List<QueryConditionChain> conditionChainHierarchy;
    private QueryConditionChain currentConditionChain;

    ConditionsConfigurer(EntityBasedQuery<T> queryBuilder) {
        this.queryBuilder = queryBuilder;
        conditionChainHierarchy = new ArrayList<>();
        currentConditionChain = queryBuilder.queryContext.getConditionChain();
        QueryConditionChain rootChain = new DefaultQueryConditionChain();
        ConditionsHelper.addCondition(currentConditionChain, ConditionChainOperator.AND, rootChain);
        currentConditionChain = rootChain;
        conditionChainHierarchy.add(currentConditionChain);
    }

    // --------------------------------------------------------------------------------------------------------
    // Public chain API + Utilities
    // --------------------------------------------------------------------------------------------------------

    public ConditionsConfigurer<T> chain() {
        return chain(ConditionChainOperator.AND);
    }

    public ConditionsConfigurer<T> and() {
        end();
        chain();
        return this;
    }

    public ConditionsConfigurer<T> or() {
        end();
        chain(ConditionChainOperator.OR);
        return this;
    }

    public ConditionsConfigurer<T> end() {
        if (currentConditionChain.getChain().size() == 0) {
            throw new IllegalStateException("Cannot close a chain without conditions");
        }
        conditionChainHierarchy.remove(currentConditionChain);
        currentConditionChain = conditionChainHierarchy.get(conditionChainHierarchy.size() - 1);
        return this;
    }

    private ConditionsConfigurer<T> chain(ConditionChainOperator chainOperator) {
        QueryConditionChain newChain = new DefaultQueryConditionChain();
        conditionChainHierarchy.add(newChain);
        ConditionsHelper.addCondition(currentConditionChain, chainOperator, newChain);
        currentConditionChain = newChain;
        return this;
    }

    // --------------------------------------------------------------------------------------------------------
    // Public conditions API
    // --------------------------------------------------------------------------------------------------------

    public ConditionsConfigurer<T> eq(String columnName, Object value) {
        return andEq(columnName, value);
    }
    public ConditionsConfigurer<T> notEq(String columnName, Object value) {
        return andNotEq(columnName, value);
    }
    public ConditionsConfigurer<T> like(String columnName, Object value) {
        return andLike(columnName, value);
    }
    public ConditionsConfigurer<T> notLike(String columnName, Object value) {
        return andNotLike(columnName, value);
    }
    public ConditionsConfigurer<T> andEq(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> andNotEq(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> andLike(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> andNotLike(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_LIKE, value);
    }
    public ConditionsConfigurer<T> orEq(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> orNotEq(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> orLike(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> orNotLike(String columnName, Object value) {
        return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_LIKE, value);
    }

    public EntityBasedQuery<T> endConditions() {
        return this.queryBuilder;
    }

    // --------------------------------------------------------------------------------------------------------
    // Global utility methods
    // --------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void checkValueType(ColumnDescriptor descriptor, Object value) {
        ColumnDescriptor currentDescriptor = descriptor;
        Class propertyType = descriptor.getPropertyType();
        while (currentDescriptor.isRelated()) {
            currentDescriptor = currentDescriptor.getRelatedColumn();
            propertyType = currentDescriptor.getPropertyType();
        }
        if (!propertyType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The condition value of column [" + descriptor.getColumnName() +
                    " (property: " + descriptor.getPropertyName() + ")] must be type of [" + propertyType + "]");
        }
    }

    @SuppressWarnings("unchecked")
    private ConditionsConfigurer<T> createSimpleCondition(ConditionChainOperator chainOperator, String columnName,
                                                          ConditionOperator operator, Object value) {
        Optional<ColumnDescriptor> columnDescriptorOptional =
                queryBuilder.entityDescriptor.getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, queryBuilder.entityDescriptor.getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        checkValueType(columnDescriptor, value);

        QueryConditionChain conditionChain = new DefaultQueryConditionWrapper(
                new DefaultAliasTableColumnExpression(
                        columnDescriptor.getColumnType(),
                        new DefaultAliasTableExpression(
                                queryBuilder.entityDescriptor.getTableName(),
                                queryBuilder.getFirstEntityDescriptorAlias()),
                        columnDescriptor.getColumnName()),
                operator,
                new DefaultConstantExpression(
                        columnDescriptor.getColumnType(),
                        columnDescriptor.getTypeManager().convertToDatabase(value)));
        ConditionsHelper.addCondition(currentConditionChain, chainOperator, conditionChain);
        return this;
    }
}
