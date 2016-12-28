package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.SimpleTableExpression;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultSimpleTableExpressionWriter")
@Scope("prototype")
@Writes(SimpleTableExpression.class)
public class DefaultSimpleTableExpressionWriter extends AbstractTableExpressionWriter<SimpleTableExpression> {
}
