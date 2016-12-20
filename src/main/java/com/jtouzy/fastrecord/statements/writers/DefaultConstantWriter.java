package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConstantContext;

@Writes(ConstantContext.class)
public class DefaultConstantWriter extends BaseConstantWriter<ConstantContext> {
    public DefaultConstantWriter(WriterCache writerCache, ConstantContext context) {
        super(writerCache, context);
    }
}
