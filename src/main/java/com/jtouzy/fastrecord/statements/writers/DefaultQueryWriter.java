package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.QueryContext;

@Writes(QueryContext.class)
public class DefaultQueryWriter extends BaseQueryWriter<QueryContext> {
    public DefaultQueryWriter(WriterCache writerCache, QueryContext context) {
        super(writerCache, context);
    }
}
