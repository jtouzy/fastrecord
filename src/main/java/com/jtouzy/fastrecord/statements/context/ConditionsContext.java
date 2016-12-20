package com.jtouzy.fastrecord.statements.context;

import com.jtouzy.fastrecord.utils.Chain;

public interface ConditionsContext {
    void addConditionContext(ConditionContext conditionContext);
    void addConditionContext(ConditionsOperator operator, ConditionContext conditionContext);
    Chain<ConditionContext,ConditionsOperator> getConditions();
}
