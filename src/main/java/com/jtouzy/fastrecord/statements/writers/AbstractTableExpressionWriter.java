package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.statements.context.TableExpression;

public abstract class AbstractTableExpressionWriter<T extends TableExpression> extends AbstractWriter<T> {
    @Override
    public void write() {
        if (!Strings.isNullOrEmpty(getContext().getSchema())) {
            getResult().getSqlString().append(getContext().getSchema()).append(".");
        }
        getResult().getSqlString().append(getContext().getTable());
    }
}
