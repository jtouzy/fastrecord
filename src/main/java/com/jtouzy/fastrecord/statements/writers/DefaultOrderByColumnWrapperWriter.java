package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.OrderByColumnWrapper;
import com.jtouzy.fastrecord.utils.Priority;

@Writes(value = OrderByColumnWrapper.class, priority = Priority.NATIVE)
public class DefaultOrderByColumnWrapperWriter extends AbstractWriter<OrderByColumnWrapper> {
    @Override
    public void write() {
        mergeWriter(getContext().getColumn());
        getResult().getSqlString().append(" ").append(getContext().getType());
    }
}
