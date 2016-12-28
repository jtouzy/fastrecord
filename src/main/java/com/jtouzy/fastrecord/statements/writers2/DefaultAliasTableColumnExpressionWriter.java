package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultAliasTableColumnExpressionWriter")
@Scope("prototype")
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
