package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(AliasTableColumnContext.class)
public class DefaultAliasTableColumnWriter extends AbstractWriter<AliasTableColumnContext> {
    public DefaultAliasTableColumnWriter(WriterCache writerCache, AliasTableColumnContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        StringBuilder sqlString = getSqlString();
        String alias = getContext().getTableContext().getTableAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            sqlString.append(alias);
        } else {
            sqlString.append(getContext().getTableContext().getTable());
        }
        sqlString.append(".")
                 .append(getContext().getColumn());
        alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            sqlString.append(" as ")
                     .append(alias);
        }
        return buildMetadata();
    }
}
