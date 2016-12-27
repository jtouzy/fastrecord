package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.TypedExpression;

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
