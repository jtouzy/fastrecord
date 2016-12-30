package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.UpdateConditionChain;
import com.jtouzy.fastrecord.statements.context.WriteWithConditionsExpression;

public class AbstractWriteWithConditionsExpression
        extends AbstractWriteExpression implements WriteWithConditionsExpression {
    private final UpdateConditionChain conditionChain;

    public AbstractWriteWithConditionsExpression(SimpleTableExpression target) {
        super(target);
        conditionChain = new DefaultUpdateConditionChain();
    }

    @Override
    public UpdateConditionChain getConditionChain() {
        return conditionChain;
    }
}
