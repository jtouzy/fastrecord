package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = AliasTableColumnExpression.class, priority = Priority.NATIVE)
public class DefaultAliasTableColumnExpressionWriter
        extends AbstractTableColumnExpressionWriter<AliasTableColumnExpression> {
}
