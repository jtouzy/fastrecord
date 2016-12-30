package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultInsertExpression implements InsertExpression {
    private final SimpleTableExpression target;
    private final Map<SimpleTableColumnExpression,String> values;

    public DefaultInsertExpression(SimpleTableExpression target) {
        this.target = target;
        this.values = new LinkedHashMap<>();
    }

    @Override
    public SimpleTableExpression getTarget() {
        return target;
    }

    @Override
    public Map<SimpleTableColumnExpression, String> getValues() {
        return values;
    }
}
