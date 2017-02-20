package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.OrderByColumnWrapper;
import com.jtouzy.fastrecord.statements.context.OrderByType;

public class DefaultOrderByColumnWrapper implements OrderByColumnWrapper {
    private OrderByType type;
    private AliasTableColumnExpression columnExpression;

    public DefaultOrderByColumnWrapper(AliasTableColumnExpression columnExpression) {
        this(columnExpression, OrderByType.DESC);
    }

    public DefaultOrderByColumnWrapper(AliasTableColumnExpression columnExpression, OrderByType type) {
        this.columnExpression = columnExpression;
        this.type = type;
    }

    @Override
    public OrderByType getType() {
        return type;
    }

    @Override
    public AliasTableColumnExpression getColumn() {
        return columnExpression;
    }
}
