package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConstantExpression;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
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
        Iterator<Map.Entry<SimpleTableColumnExpression,ConstantExpression>> it =
                getContext().getValues().entrySet().iterator();
        Map.Entry<SimpleTableColumnExpression,ConstantExpression> valueEntry;
        while (it.hasNext()) {
            valueEntry = it.next();
            // Cannot call mergeWriter because of table_name, it will be added and we don't
            // want table alias in INSERT expressions
            getResult().getSqlString().append(valueEntry.getKey().getColumn());
            if (it.hasNext()) {
                getResult().getSqlString().append(", ");
            }
        }
        metadata.getSqlString().append(") VALUES (");
        it = getContext().getValues().entrySet().iterator();
        while (it.hasNext()) {
            valueEntry = it.next();
            mergeWriter(valueEntry.getValue());
            if (it.hasNext()) {
                getResult().getSqlString().append(", ");
            }
        }
        metadata.getSqlString().append(")");
    }
}
