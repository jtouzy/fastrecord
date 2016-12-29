package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.TableColumnExpression;
import com.jtouzy.fastrecord.statements.context.TableExpression;

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
