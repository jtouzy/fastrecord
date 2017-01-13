package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.AggregateFunctionExpression;
import com.jtouzy.fastrecord.statements.context.AggregateFunctionType;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;

public class DefaultAggregateFunctionExpression implements AggregateFunctionExpression {
    private final AggregateFunctionType type;
    private final AliasTableColumnExpression column;

    public DefaultAggregateFunctionExpression(AggregateFunctionType type, AliasTableColumnExpression column) {
        this.type = type;
        this.column = column;
    }

    @Override
    public AggregateFunctionType getType() {
        return type;
    }

    @Override
    public AliasTableColumnExpression getColumn() {
        return column;
    }
}
