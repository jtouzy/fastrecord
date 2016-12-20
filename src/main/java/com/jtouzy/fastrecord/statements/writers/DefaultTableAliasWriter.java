package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.TableAliasContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(TableAliasContext.class)
public class DefaultTableAliasWriter extends AbstractWriter<TableAliasContext> {
    public DefaultTableAliasWriter(WriterCache writerCache, TableAliasContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        getSqlString().append(getContext().getTable());
        String alias = getContext().getTableAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            getSqlString().append(" ")
                          .append(getContext().getTableAlias());
        }
        return buildMetadata();
    }
}
