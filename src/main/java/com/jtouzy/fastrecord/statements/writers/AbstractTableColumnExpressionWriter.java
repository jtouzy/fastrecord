package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.TableColumnExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public abstract class AbstractTableColumnExpressionWriter<T extends TableColumnExpression> extends AbstractWriter<T> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        mergeWriter(getContext().getTableExpression());
        metadata.getSqlString().append(".")
                               .append(getContext().getColumn());
    }
}
