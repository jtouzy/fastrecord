package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;

@Writes(SimpleTableExpression.class)
public class DefaultSimpleTableExpressionWriter extends AbstractTableExpressionWriter<SimpleTableExpression> {
}
