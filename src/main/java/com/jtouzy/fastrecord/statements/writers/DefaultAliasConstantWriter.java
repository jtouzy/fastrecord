package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasConstantContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(AliasConstantContext.class)
public class DefaultAliasConstantWriter extends BaseConstantWriter<AliasConstantContext> {
    public DefaultAliasConstantWriter(WriterCache writerCache, AliasConstantContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        String alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            getSqlString().append(" as ")
                          .append(alias);
        }
        return buildMetadata();
    }
}
