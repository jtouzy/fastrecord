package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.PropertyNotFoundException;
import com.jtouzy.fastrecord.statements.context.BaseConditionContext;
import com.jtouzy.fastrecord.statements.context.BaseConstantContext;
import com.jtouzy.fastrecord.statements.context.BaseTableColumnContext;
import com.jtouzy.fastrecord.statements.context.ConditionContext;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionsOperator;

import java.util.Optional;

public class ConditionsConfigurer<T> {
    private final Query<T> queryBuilder;

    ConditionsConfigurer(Query<T> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    private void addCondition(ConditionsOperator operator, ConditionContext conditionContext) {
        if (operator != null) {
            queryBuilder.queryContext.getConditionsContext().addConditionContext(operator, conditionContext);
        } else {
            queryBuilder.queryContext.getConditionsContext().addConditionContext(conditionContext);
        }
    }

    private ConditionsConfigurer<T> createSimpleCondition(ConditionsOperator conditionsOperator, String propertyName,
                                                          ConditionOperator operator, String value) {
        Optional<ColumnDescriptor> columnDescriptorOptional =
                queryBuilder.entityDescriptor.getColumnDescriptor(propertyName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new PropertyNotFoundException(propertyName, queryBuilder.entityDescriptor.getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        ConditionContext conditionContext = new BaseConditionContext(operator);
        conditionContext.addFirstExpression(new BaseTableColumnContext("",
                queryBuilder.entityDescriptor.getTableName(), columnDescriptor.getColumnName(),
                columnDescriptor.getColumnType()));
        conditionContext.addCompareExpression(new BaseConstantContext(value, columnDescriptor.getColumnType()));
        addCondition(conditionsOperator, conditionContext);
        return this;
    }

    private ConditionsConfigurer<T> createSimpleCondition(String propertyName, ConditionOperator operator,
                                                          String value) {
        return createSimpleCondition(null, propertyName, operator, value);
    }

    public ConditionsConfigurer<T> whereEq(String propertyName, String value) {
        return createSimpleCondition(propertyName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> whereNotEq(String propertyName, String value) {
        return createSimpleCondition(propertyName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> whereLike(String propertyName, String value) {
        return createSimpleCondition(propertyName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> whereNotLike(String propertyName, String value) {
        return createSimpleCondition(propertyName, ConditionOperator.NOT_LIKE, value);
    }
    public ConditionsConfigurer<T> andEq(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, propertyName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> andNotEq(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, propertyName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> andLike(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, propertyName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> andNotLike(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.AND, propertyName, ConditionOperator.NOT_LIKE, value);
    }
    public ConditionsConfigurer<T> orEq(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, propertyName, ConditionOperator.EQUALS, value);
    }
    public ConditionsConfigurer<T> orNotEq(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, propertyName, ConditionOperator.NOT_EQUALS, value);
    }
    public ConditionsConfigurer<T> orLike(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, propertyName, ConditionOperator.LIKE, value);
    }
    public ConditionsConfigurer<T> orNotLike(String propertyName, String value) {
        return createSimpleCondition(ConditionsOperator.OR, propertyName, ConditionOperator.NOT_LIKE, value);
    }

    public Query<T> end() {
        return this.queryBuilder;
    }
}
