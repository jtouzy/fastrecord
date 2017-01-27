package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.TableExpression;

public abstract class AbstractTableExpression implements TableExpression {
    private final String schema;
    private final String table;

    public AbstractTableExpression(String table) {
        this("", table);
    }

    public AbstractTableExpression(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getSchema() {
        return schema;
    }
}
