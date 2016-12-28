package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.BasicConditionExpression;
import com.jtouzy.fastrecord.statements.context2.ConditionChain;
import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.ConditionWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConditionWrapper<E extends BasicConditionExpression, C extends ConditionChain<C>>
        extends AbstractConditionChain<C> implements ConditionWrapper<E> {

    private final List<E> firstConditionExpressionList;
    private final ConditionOperator conditionOperator;
    private final List<E> compareConditionExpressionList;

    AbstractConditionWrapper(E firstConditionExpression, ConditionOperator conditionOperator,
                             E compareConditionExpression) {
        firstConditionExpressionList = new ArrayList<>();
        firstConditionExpressionList.add(firstConditionExpression);
        this.conditionOperator = conditionOperator;
        compareConditionExpressionList = new ArrayList<>();
        compareConditionExpressionList.add(compareConditionExpression);
    }

    @Override
    public List<E> getFirstConditionExpressions() {
        return firstConditionExpressionList;
    }

    @Override
    public ConditionOperator getConditionOperator() {
        return conditionOperator;
    }

    @Override
    public List<E> getCompareConditionExpressions() {
        return compareConditionExpressionList;
    }
}
