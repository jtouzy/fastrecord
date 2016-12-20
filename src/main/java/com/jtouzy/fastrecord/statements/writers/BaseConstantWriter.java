package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.ConstantContext;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementParameter;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public class BaseConstantWriter<T extends ConstantContext> extends AbstractWriter<T> {
    public BaseConstantWriter(WriterCache writerCache, T context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        getSqlString().append("?");
        addParameter(new BaseDbReadyStatementParameter(getContext().getValue(), getContext().getType()));
        return buildMetadata();
    }
}
