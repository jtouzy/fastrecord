package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.ConstantExpression;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultConstantExpressionWriter")
@Scope("prototype")
@Writes(ConstantExpression.class)
public class DefaultConstantExpressionWriter extends AbstractWriter<ConstantExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("?");
        metadata.addParameter(new BaseDbReadyStatementParameter(getContext().getValue(), getContext().getType()));
    }
}
