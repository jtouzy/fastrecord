package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.AliasTableExpression;

public class DefaultAliasTableExpression extends AbstractTableExpression implements AliasTableExpression {
    private final String tableAlias;

    public DefaultAliasTableExpression(String table, String tableAlias) {
        super(table);
        this.tableAlias = tableAlias;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }
}
