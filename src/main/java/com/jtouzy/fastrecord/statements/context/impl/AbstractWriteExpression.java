package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.WriteExpression;

public abstract class AbstractWriteExpression implements WriteExpression {
    private final SimpleTableExpression target;

    public AbstractWriteExpression(SimpleTableExpression target) {
        this.target = target;
    }

    @Override
    public SimpleTableExpression getTarget() {
        return target;
    }
}
