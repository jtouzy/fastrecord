package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConditionContext;
import com.jtouzy.fastrecord.statements.context.ExpressionContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;
import java.util.List;

@Writes(ConditionContext.class)
public class DefaultConditionWriter extends AbstractWriter<ConditionContext> {
    public DefaultConditionWriter(WriterCache writerCache, ConditionContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        appendExpressions(getContext().getFirstExpressions());
        appendOperator();
        appendExpressions(getContext().getCompareExpressions());
        return buildMetadata();
    }

    private void appendExpressions(List<ExpressionContext> expressions) {
        Iterator<ExpressionContext> it = expressions.iterator();
        if (expressions.size() > 1) {
            getSqlString().append('(');
        }
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                getSqlString().append(", ");
            }
        }
        if (expressions.size() > 1) {
            getSqlString().append(')');
        }
    }

    private void appendOperator() {
        switch (getContext().getOperator()) {
            case EQUALS:
                getSqlString().append(" = ");
                break;
            case NOT_EQUALS:
                getSqlString().append(" != ");
                break;
            case IN:
                getSqlString().append(" IN ");
                break;
            case NOT_IN:
                getSqlString().append(" NOT IN ");
                break;
            case LIKE:
                getSqlString().append(" LIKE ");
                break;
            case NOT_LIKE:
                getSqlString().append(" NOT LIKE ");
                break;
        }
    }
}
