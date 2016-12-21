package com.jtouzy.fastrecord.statements.context;

import com.jtouzy.fastrecord.utils.Chain;

public class BaseConditionsContext implements ConditionsContext {
    private final Chain<ConditionContext,ConditionsOperator> conditions;

    public BaseConditionsContext() {
        this.conditions = new Chain<>();
    }

    @Override
    public void addConditionContext(ConditionsOperator operator, ConditionContext conditionContext) {
        if (conditions.size() == 0)
            this.conditions.addFirst(conditionContext);
        else
            this.conditions.add(operator, conditionContext);
    }

    @Override
    public Chain<ConditionContext,ConditionsOperator> getConditions() {
        return conditions;
    }
}
