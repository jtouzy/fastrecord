package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultUpdateExpression extends AbstractWriteWithConditionsExpression implements UpdateExpression {
    private final Map<SimpleTableColumnExpression,UpdateValueExpression> values;

    public DefaultUpdateExpression(SimpleTableExpression target) {
        super(target);
        values = new LinkedHashMap<>();
    }

    @Override
    public Map<SimpleTableColumnExpression, UpdateValueExpression> getValues() {
        return values;
    }
}
