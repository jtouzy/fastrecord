package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.QueryConditionExpression;

public class DefaultQueryConditionWrapper
        extends AbstractConditionWrapper<QueryConditionExpression,QueryConditionChain> implements QueryConditionChain {

    public DefaultQueryConditionWrapper(ConditionOperator conditionOperator,
                                        QueryConditionExpression compareConditionExpression) {
        this(null, conditionOperator, compareConditionExpression);
    }

    public DefaultQueryConditionWrapper(QueryConditionExpression firstConditionExpression,
                                        ConditionOperator conditionOperator,
                                        QueryConditionExpression compareConditionExpression) {
        super(firstConditionExpression, conditionOperator, compareConditionExpression);
    }
}
