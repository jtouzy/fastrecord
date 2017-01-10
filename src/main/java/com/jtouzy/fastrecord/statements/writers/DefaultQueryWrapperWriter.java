package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.QueryWrapper;

@Writes(QueryWrapper.class)
public class DefaultQueryWrapperWriter extends AbstractWriter<QueryWrapper> {
    @Override
    public void write() {
        getResult().getSqlString().append("(");
        mergeWriter(getContext().getQueryExpression());
        getResult().getSqlString().append(")");
    }
}
