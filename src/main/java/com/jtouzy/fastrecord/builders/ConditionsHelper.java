package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;

public class ConditionsHelper {
    public static final void addCondition(QueryConditionChain chain,
                                          ConditionChainOperator chainOperator,
                                          QueryConditionChain conditionChain) {
        if (chain.getChain().size() == 0) {
            chain.addCondition(conditionChain);
        } else {
            chain.addCondition(chainOperator, conditionChain);
        }
    }
}
