package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.AliasTableExpression;

public class DefaultAliasTableColumnExpression
        extends AbstractTableColumnExpression<AliasTableExpression> implements AliasTableColumnExpression {

    public DefaultAliasTableColumnExpression(int type, AliasTableExpression tableExpression, String column) {
        super(type, tableExpression, column);
    }
}
