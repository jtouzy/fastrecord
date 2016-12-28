package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.QueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.QueryExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component("FastRecord.Writer.DefaultQueryExpressionWriter")
@Scope("prototype")
@Writes(QueryExpression.class)
public class DefaultQueryExpressionWriter extends AbstractWriter<QueryExpression> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("SELECT ");
        Iterator<QueryColumnExpressionWrapper> it = getContext().getColumns().iterator();
        while (it.hasNext()) {
            mergeWriter(it.next());
            if (it.hasNext()) {
                metadata.getSqlString().append(", ");
            }
        }
        metadata.getSqlString().append(" FROM ");
        mergeWriter(getContext().getMainTargetExpression());
        // TODO QueryTargetExpressionJoin
        if (getContext().getConditionChain().getChain().size() > 0) {
            metadata.getSqlString().append(" WHERE ");
            mergeWriter(getContext().getConditionChain());
        }
    }
}
