package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.AliasExpressionContext;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;

public class BaseQueryWriter<T extends QueryContext> extends AbstractWriter<T> {
    public BaseQueryWriter(WriterCache writerCache, T context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        appendQueryColumns();
        return buildMetadata();
    }

    private void appendQueryColumns() {
        getSqlString().append("SELECT ");
        // TODO contextValidation for no columns
        Iterator<AliasExpressionContext> it = getContext().getColumnContextList().iterator();
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                getSqlString().append(", ");
            }
        }
        getSqlString().append(" FROM ");
        // TODO append FROM
        // TODO append WHERE
    }
}
