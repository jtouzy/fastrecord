package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;

public class DefaultSimpleTableColumnExpression
        extends AbstractTableColumnExpression<SimpleTableExpression> implements SimpleTableColumnExpression {

    public DefaultSimpleTableColumnExpression(int type, SimpleTableExpression tableExpression, String column) {
        super(type, tableExpression, column);
    }
}
