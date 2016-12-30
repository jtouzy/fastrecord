package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultUpdateExpression extends AbstractWriteExpression implements UpdateExpression {
    private final Map<SimpleTableColumnExpression,UpdateValueExpression> values;
    private final UpdateConditionChain conditionChain;

    public DefaultUpdateExpression(SimpleTableExpression target) {
        super(target);
        values = new LinkedHashMap<>();
        conditionChain = new DefaultUpdateConditionChain();
    }

    @Override
    public Map<SimpleTableColumnExpression, UpdateValueExpression> getValues() {
        return values;
    }

    @Override
    public UpdateConditionChain getConditionChain() {
        return conditionChain;
    }
}
