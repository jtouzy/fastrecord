package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.TableExpression;

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
