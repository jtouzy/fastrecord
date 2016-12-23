package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.JoinContext;
import com.jtouzy.fastrecord.statements.context.JoinListContext;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.List;
import java.util.stream.Collectors;

@Writes(JoinListContext.class)
public class DefaultJoinListWriter extends AbstractWriter<JoinListContext> {
    public DefaultJoinListWriter(WriterCache writerCache, JoinListContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        getSqlString().append(" FROM ");
        mergeWriter(getContext().getMainTableContext());
        List<JoinContext> joinContextList = getContext().getJoinContextList();
        if (joinContextList.size() > 0) {
            appendJoinsFrom(getContext().getMainTableContext().getTableAlias(), joinContextList);
        }
        return buildMetadata();
    }

    private void appendJoinsFrom(String alias, List<JoinContext> joinContextList) {
        List<JoinContext> joinContextSubList = joinContextList.stream()
                .filter(jc -> jc.getFirstContext().getTableAlias().equals(alias)).collect(Collectors.toList());
        for (JoinContext joinContext : joinContextSubList) {
            if (joinContext.getJoinOperator() == JoinOperator.JOIN) {
                getSqlString().append(", ");
            }
            mergeWriter(joinContext.getSecondContext());
            appendJoinsFrom(joinContext.getSecondContext().getTableAlias(), joinContextList);
        }
    }
}
