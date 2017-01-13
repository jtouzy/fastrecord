package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AggregateFunctionExpression;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = AggregateFunctionExpression.class, priority = Priority.NATIVE)
public class DefaultAggregateFunctionExpressionWriter extends AbstractWriter<AggregateFunctionExpression> {
    @Override
    public void write() {
        switch (getContext().getType()) {
            case COUNT:
                getResult().getSqlString().append("COUNT(");
                break;
            case AVERAGE:
                getResult().getSqlString().append("AVERAGE(");
                break;
            case MAX:
                getResult().getSqlString().append("MAX(");
                break;
            case MIN:
                getResult().getSqlString().append("MIN(");
                break;
            case SUM:
                getResult().getSqlString().append("SUM(");
                break;
        }
        mergeWriter(getContext().getColumn());
        getResult().getSqlString().append(")");
    }
}
