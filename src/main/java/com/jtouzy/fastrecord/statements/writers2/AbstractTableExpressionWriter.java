package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.statements.context2.TableExpression;

public abstract class AbstractTableExpressionWriter<T extends TableExpression> extends AbstractWriter<T> {
    @Override
    public void write() {
        getResult().getSqlString().append(getContext().getTable());
    }
}
