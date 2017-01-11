package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = SimpleTableColumnExpression.class, priority = Priority.NATIVE)
public class DefaultSimpleTableColumnExpressionWriter
        extends AbstractTableColumnExpressionWriter<SimpleTableColumnExpression> {
}
