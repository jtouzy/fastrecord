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

    private ConditionsConfigurer<T> createSimpleCondition(ConditionsOperator conditionsOperator, String columnName,
                                                          ConditionOperator operator, String value) {
        Optional<ColumnDescriptor> columnDescriptorOptional =
                queryBuilder.entityDescriptor.getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, queryBuilder.entityDescriptor.getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        ConditionContext conditionContext = new BaseConditionContext(operator);
        conditionContext.addFirstExpression(new BaseTableColumnContext(queryBuilder.firstEntityDescriptorAlias,
                queryBuilder.entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                columnDescriptor.getColumnType()));
        conditionContext.addCompareExpression(new BaseConstantContext(value, columnDescriptor.getColumnType()));
        addCondition(conditionsOperator, conditionContext);
        return this;
    }

    public ConditionsConfigurer<T> eq(String columnName, String value) {
        return andEq(columnName, value);
    }
    public ConditionsConfigurer<T> notEq(String columnName, String value) {
        return andNotEq(columnName, value);
    }
    public ConditionsConfigurer<T> like(String columnName, String value) {
        return andLike(columnName, value);
    }
    public ConditionsConfigurer<T> notLike(String columnName, String value) {
        return andNotLike(columnName, value);
    }
    public ConditionsConfigurer<T> andEq(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> andNotEq(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> andLike(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> andNotLike(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, columnName, ConditionOperator.NOT_LIKE, value);
    }
    public ConditionsConfigurer<T> orEq(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> orNotEq(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> orLike(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> orNotLike(String columnName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, columnName, ConditionOperator.NOT_LIKE, value);
    }

    public EntityBasedQuery<T> end() {
        return this.queryBuilder;
    }
}
