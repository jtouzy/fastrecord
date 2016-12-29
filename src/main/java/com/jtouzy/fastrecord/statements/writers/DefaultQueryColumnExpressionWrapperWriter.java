package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.QueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultQueryColumnExpressionWrapperWriter")
@Scope("prototype")
@Writes(QueryColumnExpressionWrapper.class)
public class DefaultQueryColumnExpressionWrapperWriter extends AbstractWriter<QueryColumnExpressionWrapper> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        mergeWriter(getContext().getExpression());
        String alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            metadata.getSqlString().append(" as ").append(alias);
        }
    }
}
