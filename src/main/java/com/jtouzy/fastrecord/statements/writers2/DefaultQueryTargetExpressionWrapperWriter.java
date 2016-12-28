package com.jtouzy.fastrecord.statements.writers2;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.QueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultQueryTargetExpressionWrapperWriter")
@Scope("prototype")
@Writes(QueryTargetExpressionWrapper.class)
public class DefaultQueryTargetExpressionWrapperWriter extends AbstractWriter<QueryTargetExpressionWrapper> {
    @Override
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        mergeWriter(getContext().getExpression());
        String alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            metadata.getSqlString().append(" ").append(alias);
        }
    }
}
