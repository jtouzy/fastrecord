package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.DeleteExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = DeleteExpression.class, priority = Priority.NATIVE)
public class DefaultDeleteExpressionWriter extends AbstractConditionChainHolderWriter<DeleteExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("DELETE FROM ");
        mergeWriter(getContext().getTarget());
        writeConditions();
    }
}
