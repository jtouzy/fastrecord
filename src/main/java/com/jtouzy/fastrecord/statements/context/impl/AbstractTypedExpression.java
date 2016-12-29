package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.TypedExpression;

public abstract class AbstractTypedExpression implements TypedExpression {
    private final int type;

    public AbstractTypedExpression(int type) {
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }
}
