package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.AliasTableExpression;

public class DefaultAliasTableColumnExpression
        extends AbstractTableColumnExpression<AliasTableExpression> implements AliasTableColumnExpression {

    public DefaultAliasTableColumnExpression(int type, AliasTableExpression tableExpression, String column) {
        super(type, tableExpression, column);
    }
}
