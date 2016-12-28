package com.jtouzy.fastrecord.statements.writers2;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.AliasTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultAliasTableExpressionWriter")
@Scope("prototype")
@Writes(AliasTableExpression.class)
public class DefaultAliasTableExpressionWriter extends AbstractTableExpressionWriter<AliasTableExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        String tableAlias = getContext().getTableAlias();
        if (Strings.isNullOrEmpty(tableAlias)) {
            super.write();
        } else {
            metadata.getSqlString().append(tableAlias);
        }
    }
}
