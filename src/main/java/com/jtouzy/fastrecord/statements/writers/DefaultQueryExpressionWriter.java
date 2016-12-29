package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.QueryTargetExpressionJoin;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component("FastRecord.Writer.DefaultQueryExpressionWriter")
@Scope("prototype")
@Writes(QueryExpression.class)
public class DefaultQueryExpressionWriter extends AbstractWriter<QueryExpression> {
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
        if (getContext().getConditionChain().getChain().size() > 0) {
            metadata.getSqlString().append(" WHERE ");
            mergeWriter(getContext().getConditionChain());
        }
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
}
