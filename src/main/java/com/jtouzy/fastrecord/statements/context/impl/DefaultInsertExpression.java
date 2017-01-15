package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.ConstantExpression;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultInsertExpression extends AbstractWriteExpression implements InsertExpression {
    private final Map<SimpleTableColumnExpression,ConstantExpression> values;

    public DefaultInsertExpression(SimpleTableExpression target) {
        super(target);
        values = new LinkedHashMap<>();
    }

    @Override
    public Map<SimpleTableColumnExpression, ConstantExpression> getValues() {
        return values;
    }
}
