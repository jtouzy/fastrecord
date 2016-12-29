package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(AliasTableColumnExpression.class)
public class DefaultAliasTableColumnExpressionWriter extends AbstractWriter<AliasTableColumnExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        mergeWriter(getContext().getTableExpression());
        metadata.getSqlString().append(".")
                               .append(getContext().getColumn());
    }
}
