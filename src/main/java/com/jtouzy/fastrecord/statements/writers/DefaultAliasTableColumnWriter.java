package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(AliasTableColumnContext.class)
public class DefaultAliasTableColumnWriter extends BaseTableColumnWriter<AliasTableColumnContext> {
    public DefaultAliasTableColumnWriter(WriterCache writerCache, AliasTableColumnContext context) {
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
