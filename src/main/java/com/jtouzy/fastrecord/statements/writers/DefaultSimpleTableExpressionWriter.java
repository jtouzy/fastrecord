package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = SimpleTableExpression.class, priority = Priority.NATIVE)
public class DefaultSimpleTableExpressionWriter extends AbstractTableExpressionWriter<SimpleTableExpression> {
}
