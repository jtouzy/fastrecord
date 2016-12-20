package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.TableColumnContext;

@Writes(TableColumnContext.class)
public class DefaultTableColumnWriter extends BaseTableColumnWriter<TableColumnContext> {
    public DefaultTableColumnWriter(WriterCache writerCache, TableColumnContext context) {
        super(writerCache, context);
    }
}
