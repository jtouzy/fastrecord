package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context2.QueryConditionExpression;

public class DefaultQueryConditionWrapper
        extends AbstractConditionWrapper<QueryConditionExpression,QueryConditionChain> implements QueryConditionChain {

    public DefaultQueryConditionWrapper(QueryConditionExpression firstConditionExpression,
                                        ConditionOperator conditionOperator,
                                        QueryConditionExpression compareConditionExpression) {
        super(firstConditionExpression, conditionOperator, compareConditionExpression);
    }
}
