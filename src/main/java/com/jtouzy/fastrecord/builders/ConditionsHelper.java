package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;

public class ConditionsHelper {
    @SuppressWarnings("unchecked")
    public static final void addCondition(ConditionChain chain,
                                          ConditionChainOperator chainOperator,
                                          ConditionChain conditionChain) {
        if (chain.getChain().size() == 0) {
            chain.addCondition(conditionChain);
        } else {
            chain.addCondition(chainOperator, conditionChain);
        }
    }
}
