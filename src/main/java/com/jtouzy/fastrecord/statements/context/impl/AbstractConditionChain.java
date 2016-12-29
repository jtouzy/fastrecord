package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.utils.Chain;

public abstract class AbstractConditionChain<T extends ConditionChain<T>> implements ConditionChain<T> {
    private final Chain<T, ConditionChainOperator> chain;

    AbstractConditionChain() {
        this.chain = new Chain<>();
    }

    @Override
    public void addCondition(T conditionChain) {
        if (chain.size() != 0) {
            throw new IllegalStateException("Conditions already exists in condition chain. " +
                    "You must call the addCondition(operator,conditionChain) method.");
        }
        chain.addFirst(conditionChain);
    }

    @Override
    public void addCondition(ConditionChainOperator operator, T conditionChain) {
        if (chain.size() == 0) {
            throw new IllegalStateException("No conditions in the condition chain. " +
                    "You must call the addCondition(conditionChain) method first.");
        }
        chain.add(operator, conditionChain);
    }

    @Override
    public Chain<T, ConditionChainOperator> getChain() {
        return chain;
    }
}
