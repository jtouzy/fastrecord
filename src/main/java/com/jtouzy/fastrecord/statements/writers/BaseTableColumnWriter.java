package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.TableColumnContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public class BaseTableColumnWriter<T extends TableColumnContext> extends AbstractWriter<T> {
    public BaseTableColumnWriter(WriterCache writerCache, T context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        mergeWriter(getContext().getTableContext());
        getSqlString().append(".")
                      .append(getContext().getColumn());
        return buildMetadata();
    }
}
