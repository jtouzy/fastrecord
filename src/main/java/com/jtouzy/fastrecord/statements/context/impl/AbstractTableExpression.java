package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.TableExpression;

public abstract class AbstractTableExpression implements TableExpression {
    private final String table;

    public AbstractTableExpression(String table) {
        this.table = table;
    }

    @Override
    public String getTable() {
        return table;
    }
}
