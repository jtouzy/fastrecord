package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;

@Writes(SimpleTableColumnExpression.class)
public class DefaultSimpleTableColumnExpressionWriter
        extends AbstractTableColumnExpressionWriter<SimpleTableColumnExpression> {
}
