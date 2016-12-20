package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.AliasExpressionContext;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.context.QueryFromContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Chain;

import java.util.Iterator;

public class BaseQueryWriter<T extends QueryContext> extends AbstractWriter<T> {
    public BaseQueryWriter(WriterCache writerCache, T context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        appendColumns();
        appendFrom();
        return buildMetadata();
    }

    private void appendColumns() {
        getSqlString().append("SELECT ");
        // TODO contextValidation for no columns
        Iterator<AliasExpressionContext> it = getContext().getColumnContextList().iterator();
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                getSqlString().append(", ");
            }
        }
    }

    private void appendFrom() {
        getSqlString().append(" FROM ");
        // TODO contextValidation for no from
        Iterator<Chain.ChainItemWrapper<QueryFromContext,JoinOperator>> itf =
                getContext().getFromContextChain().iterator();
        while (itf.hasNext()) {
            mergeWriter(itf.next().getItem());
            if (itf.hasNext()) {
                getSqlString().append(", ");
            }
        }
    }
}
