package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.TableExpression;

public abstract class AbstractTableExpressionWriter<T extends TableExpression> extends AbstractWriter<T> {
    @Override
    public void write() {
        getResult().getSqlString().append(getContext().getTable());
    }
}
