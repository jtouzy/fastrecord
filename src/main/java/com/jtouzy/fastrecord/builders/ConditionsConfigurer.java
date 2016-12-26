package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.statements.context.BaseConditionContext;
import com.jtouzy.fastrecord.statements.context.BaseConstantContext;
import com.jtouzy.fastrecord.statements.context.BaseTableColumnContext;
import com.jtouzy.fastrecord.statements.context.ConditionContext;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionsOperator;

import java.util.Optional;

public class ConditionsConfigurer<T> {
    private final EntityBasedQuery<T> queryBuilder;

    ConditionsConfigurer(EntityBasedQuery<T> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    private void addCondition(ConditionsOperator operator, ConditionContext conditionContext) {
        queryBuilder.queryContext.getConditionsContext().addConditionContext(operator, conditionContext);
    }

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
    private ConditionsConfigurer<T> createSimpleCondition(ConditionsOperator conditionsOperator, String columnName,
                                                          ConditionOperator operator, Object value) {
        Optional<ColumnDescriptor> columnDescriptorOptional =
                queryBuilder.entityDescriptor.getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, queryBuilder.entityDescriptor.getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        checkValueType(columnDescriptor, value);
        ConditionContext conditionContext = new BaseConditionContext(operator);
        conditionContext.addFirstExpression(new BaseTableColumnContext(queryBuilder.getFirstEntityDescriptorAlias(),
                queryBuilder.entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                columnDescriptor.getColumnType()));
        conditionContext.addCompareExpression(new BaseConstantContext(
                columnDescriptor.getTypeManager().convertToDatabase(value), columnDescriptor.getColumnType()));
        addCondition(conditionsOperator, conditionContext);
        return this;
    }

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
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> andNotEq(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> andLike(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> andNotLike(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.NOT_LIKE, value);
    }
    public ConditionsConfigurer<T> orEq(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> orNotEq(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> orLike(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> orNotLike(String columnName, Object value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.NOT_LIKE, value);
    }

    public EntityBasedQuery<T> end() {
        return this.queryBuilder;
    }
}
