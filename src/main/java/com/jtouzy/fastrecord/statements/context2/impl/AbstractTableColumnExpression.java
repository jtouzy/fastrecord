package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.TableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.TableExpression;

public class AbstractTableColumnExpression<T extends TableExpression> extends AbstractTypedExpression implements TableColumnExpression<T> {
    private final T tableExpression;
    private final String column;

    public AbstractTableColumnExpression(int type, T tableExpression, String column) {
        super(type);
        this.tableExpression = tableExpression;
        this.column = column;
    }

    @Override
    public T getTableExpression() {
        return tableExpression;
    }

    @Override
    public String getColumn() {
        return column;
    }
}
