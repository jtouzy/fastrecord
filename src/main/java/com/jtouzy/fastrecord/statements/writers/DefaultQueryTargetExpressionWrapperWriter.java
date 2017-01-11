package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.QueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = QueryTargetExpressionWrapper.class, priority = Priority.NATIVE)
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
