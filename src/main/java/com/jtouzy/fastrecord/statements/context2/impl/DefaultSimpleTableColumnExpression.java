package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.SimpleTableExpression;

public class DefaultSimpleTableColumnExpression
        extends AbstractTableColumnExpression<SimpleTableExpression> implements SimpleTableColumnExpression {

    public DefaultSimpleTableColumnExpression(int type, SimpleTableExpression tableExpression, String column) {
        super(type, tableExpression, column);
    }
}
