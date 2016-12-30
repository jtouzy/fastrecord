package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;

@Writes(AliasTableColumnExpression.class)
public class DefaultAliasTableColumnExpressionWriter
        extends AbstractTableColumnExpressionWriter<AliasTableColumnExpression> {
}
