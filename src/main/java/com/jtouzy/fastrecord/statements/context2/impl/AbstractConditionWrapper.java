package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.BasicConditionExpression;
import com.jtouzy.fastrecord.statements.context2.ConditionChain;
import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.ConditionWrapper;

public abstract class AbstractConditionWrapper<E extends BasicConditionExpression, C extends ConditionChain<C>>
        extends AbstractConditionChain<C> implements ConditionWrapper<E> {

    private final E firstConditionExpression;
    private final ConditionOperator conditionOperator;
    private final E compareConditionExpression;

    AbstractConditionWrapper(E firstConditionExpression, ConditionOperator conditionOperator,
                             E compareConditionExpression) {
        this.firstConditionExpression = firstConditionExpression;
        this.conditionOperator = conditionOperator;
        this.compareConditionExpression = compareConditionExpression;
    }

    @Override
    public E getFirstConditionExpression() {
        return firstConditionExpression;
    }

    @Override
    public ConditionOperator getConditionOperator() {
        return conditionOperator;
    }

    @Override
    public E getCompareConditionExpression() {
        return compareConditionExpression;
    }
}
