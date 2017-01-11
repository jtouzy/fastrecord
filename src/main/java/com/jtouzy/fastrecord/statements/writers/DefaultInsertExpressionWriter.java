package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Priority;

import java.util.Iterator;
import java.util.Map;

@Writes(value = InsertExpression.class, priority = Priority.NATIVE)
public class DefaultInsertExpressionWriter extends AbstractWriter<InsertExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("INSERT INTO ");
        mergeWriter(getContext().getTarget());
        metadata.getSqlString().append(" (");
        Iterator<Map.Entry<SimpleTableColumnExpression,String>> it = getContext().getValues().entrySet().iterator();
        Map.Entry<SimpleTableColumnExpression,String> valueEntry;
        StringBuilder columnsString = new StringBuilder();
        StringBuilder valuesString = new StringBuilder();
        while (it.hasNext()) {
            valueEntry = it.next();
            columnsString.append(valueEntry.getKey().getColumn());
            valuesString.append("?");
            metadata.getParameters().add(
                    new BaseDbReadyStatementParameter(valueEntry.getValue(), valueEntry.getKey().getType()));
            if (it.hasNext()) {
                columnsString.append(", ");
                valuesString.append(", ");
            }
        }
        metadata.getSqlString().append(columnsString).append(") VALUES (").append(valuesString).append(")");
    }
}
