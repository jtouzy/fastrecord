package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.ConstantExpression;

public class DefaultConstantExpression extends AbstractTypedExpression implements ConstantExpression {
    private final String value;

    public DefaultConstantExpression(int type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
