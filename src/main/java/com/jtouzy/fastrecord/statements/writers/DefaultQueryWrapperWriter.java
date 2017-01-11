package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.QueryWrapper;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = QueryWrapper.class, priority = Priority.NATIVE)
public class DefaultQueryWrapperWriter extends AbstractWriter<QueryWrapper> {
    @Override
    public void write() {
        getResult().getSqlString().append("(");
        mergeWriter(getContext().getQueryExpression());
        getResult().getSqlString().append(")");
    }
}
