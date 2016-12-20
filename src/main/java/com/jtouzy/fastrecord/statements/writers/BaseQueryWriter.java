package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.AliasExpressionContext;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryContext;
import com.jtouzy.fastrecord.statements.context.QueryFromContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class BaseQueryWriter<T extends QueryContext> extends AbstractWriter<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseQueryWriter.class);

    public BaseQueryWriter(WriterCache writerCache, T context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        appendColumns();
        appendFrom();
        appendWhere();
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

    private void appendWhere() {
        if (getContext().getConditionsContext().getConditions().size() > 0) {
            mergeWriter(getContext().getConditionsContext());
        } else {
            logger.debug("No conditions to append. No need to call ConditionsContextWriter.");
        }
    }
}
