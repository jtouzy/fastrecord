package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.statements.context.AliasConstantContext;
import com.jtouzy.fastrecord.statements.processing.BaseDbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

import java.sql.Types;

public class DefaultAliasConstantWriter extends AbstractWriter<AliasConstantContext> {
    public DefaultAliasConstantWriter(AliasConstantContext context) {
        super(context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        StringBuilder sqlString = getSqlString();
        boolean isVarchar = getContext().getType() == Types.VARCHAR;
        if (isVarchar) {
            sqlString.append("'");
        }
        sqlString.append(getContext().getValue());
        if (isVarchar) {
            sqlString.append("'");
        }
        String alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            sqlString.append(" as ")
                    .append(alias);
        }
        return new BaseDbReadyStatementMetadata(sqlString.toString());
    }
}
