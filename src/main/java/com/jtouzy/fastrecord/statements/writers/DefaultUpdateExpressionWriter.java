package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.UpdateValueExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;
import java.util.Map;

@Writes(UpdateExpression.class)
public class DefaultUpdateExpressionWriter extends AbstractWriter<UpdateExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("UPDATE ");
        mergeWriter(getContext().getTarget());
        metadata.getSqlString().append(" SET ");
        Iterator<Map.Entry<SimpleTableColumnExpression,UpdateValueExpression>> it =
                getContext().getValues().entrySet().iterator();
        Map.Entry<SimpleTableColumnExpression,UpdateValueExpression> valueEntry;
        while (it.hasNext()) {
            valueEntry = it.next();
            mergeWriter(valueEntry.getKey());
            metadata.getSqlString().append(" = ");
            mergeWriter(valueEntry.getValue());
            if (it.hasNext()) {
                metadata.getSqlString().append(", ");
            }
        }
        if (getContext().getConditionChain().getChain().size() > 0) {
            metadata.getSqlString().append(" WHERE ");
            mergeWriter(getContext().getConditionChain());
        }
    }
}
