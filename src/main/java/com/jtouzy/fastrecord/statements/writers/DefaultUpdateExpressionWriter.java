package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.UpdateValueExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.util.Iterator;
import java.util.Map;

@Writes(UpdateExpression.class)
public class DefaultUpdateExpressionWriter extends AbstractConditionChainHolderWriter<UpdateExpression> {
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
            // Cannot call mergeWriter because of table_name, it will be added and we don't
            // want table alias in UPDATE expressions
            getResult().getSqlString().append(valueEntry.getKey().getColumn()).append(" = ");
            mergeWriter(valueEntry.getValue());
            if (it.hasNext()) {
                metadata.getSqlString().append(", ");
            }
        }
        writeConditions();
    }
}
