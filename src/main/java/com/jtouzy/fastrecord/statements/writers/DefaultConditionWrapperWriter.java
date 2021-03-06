package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.BasicConditionExpression;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Priority;

import java.util.Iterator;
import java.util.List;

@Writes(value = ConditionWrapper.class, priority = Priority.NATIVE)
public class DefaultConditionWrapperWriter extends AbstractWriter<ConditionWrapper> {
    @Override
    @SuppressWarnings("unchecked")
    public void write() {
        if (getContext().getConditionOperator().hasFirstExpression()) {
            appendExpressionList(getContext().getFirstConditionExpressions());
        }
        appendOperator();
        appendExpressionList(getContext().getCompareConditionExpressions());
    }

    @SuppressWarnings("unchecked")
    private void appendExpressionList(List<BasicConditionExpression> expressionList) {
        DbReadyStatementMetadata metadata = getResult();
        if (expressionList.size() > 1) {
            metadata.getSqlString().append("(");
        }
        Iterator<BasicConditionExpression> it = expressionList.iterator();
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                metadata.getSqlString().append(", ");
            }
        }
        if (expressionList.size() > 1) {
            metadata.getSqlString().append(")");
        }
    }

    private void appendOperator() {
        DbReadyStatementMetadata metadata = getResult();
        ConditionOperator conditionOperator = getContext().getConditionOperator();
        if (conditionOperator.hasFirstExpression()) {
            metadata.getSqlString().append(" ");
        }
        switch (conditionOperator) {
            case EQUALS:
                metadata.getSqlString().append("= ");
                break;
            case NOT_EQUALS:
                metadata.getSqlString().append("!= ");
                break;
            case IN:
                metadata.getSqlString().append("IN ");
                break;
            case NOT_IN:
                metadata.getSqlString().append("NOT IN ");
                break;
            case LIKE:
                metadata.getSqlString().append("LIKE ");
                break;
            case NOT_LIKE:
                metadata.getSqlString().append("NOT LIKE ");
                break;
            case EXISTS:
                metadata.getSqlString().append("EXISTS ");
                break;
            case NOT_EXISTS:
                metadata.getSqlString().append("NOT EXISTS ");
                break;
        }
    }
}
