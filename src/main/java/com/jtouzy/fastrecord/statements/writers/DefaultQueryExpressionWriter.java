package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.*;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Writes(QueryExpression.class)
public class DefaultQueryExpressionWriter extends AbstractConditionChainHolderWriter<QueryExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        // Query columns
        metadata.getSqlString().append("SELECT ");
        Iterator<QueryColumnExpressionWrapper> it = getContext().getColumns().iterator();
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                metadata.getSqlString().append(", ");
            }
        }
        // Query target
        metadata.getSqlString().append(" FROM ");
        mergeWriter(getContext().getMainTargetExpression());
        if (getContext().getTargetJoinList().size() > 0) {
            appendJoinsFrom(getContext().getMainTargetExpression().getAlias());
        }
        // Query conditions
        writeConditions();
        // Query order
        appendQueryOrder();
    }

    private void appendJoinsFrom(String alias) {
        List<QueryTargetExpressionJoin> joinSubList = getContext().getTargetJoinList().stream()
                .filter(j -> j.getFirstTargetExpression().getAlias().equals(alias)).collect(Collectors.toList());
        for (QueryTargetExpressionJoin joinExpression : joinSubList) {
            if (joinExpression.getJoinOperator() == JoinOperator.JOIN) {
                getResult().getSqlString().append(", ");
            }
            mergeWriter(joinExpression.getJoinTargetExpression());
            appendJoinsFrom(joinExpression.getJoinTargetExpression().getAlias());
        }
    }

    private void appendQueryOrder() {
        if (!getContext().getOrderByColumns().isEmpty()) {
            getResult().getSqlString().append(" ORDER BY ");
            Iterator<AliasTableColumnExpression> it = getContext().getOrderByColumns().iterator();
            while (it.hasNext()) {
                mergeWriter(it.next());
                if (it.hasNext()) {
                    getResult().getSqlString().append(", ");
                }
            }
        }
    }
}
