package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConstantExpression;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = ConstantExpression.class, priority = Priority.NATIVE)
public class DefaultConstantExpressionWriter extends AbstractWriter<ConstantExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("?");
        metadata.addParameter(new BaseDbReadyStatementParameter(getContext().getValue(), getContext().getType()));
    }
}
