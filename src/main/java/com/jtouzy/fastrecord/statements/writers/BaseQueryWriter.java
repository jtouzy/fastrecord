package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.AliasExpressionContext;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;

public class BaseQueryWriter<T extends QueryContext> extends AbstractWriter<T> {
    public BaseQueryWriter(T context) {
        super(context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        appendQueryColumns();
        return new BaseDbReadyStatementMetadata(getSqlString().toString());
    }

    private void appendQueryColumns() {
        getSqlString().append("SELECT ");
        Iterator<AliasExpressionContext> it = getContext().getColumnContextList().iterator();
        while (it.hasNext()) {
            appendSqlStringFromWriter(it.next());
            if (it.hasNext()) {
                getSqlString().append(", ");
            }
        }
    }
}
