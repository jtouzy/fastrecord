package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.AliasTableExpression;

public class DefaultAliasTableExpression extends AbstractTableExpression implements AliasTableExpression {
    private final String tableAlias;

    public DefaultAliasTableExpression(String table, String tableAlias) {
        this("", table, tableAlias);
    }

    public DefaultAliasTableExpression(String schema, String table, String tableAlias) {
        super(schema, table);
        this.tableAlias = tableAlias;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }
}
