package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.AliasQueryContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

@Writes(AliasQueryContext.class)
public class DefaultAliasQueryWriter extends BaseQueryWriter<AliasQueryContext> {
    public DefaultAliasQueryWriter(AliasQueryContext context) {
        super(context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        // TODO: Objet DbReady recréé : optimisations?
        super.write();
        getSqlString().insert(0, '(');
        getSqlString().append(')');
        String alias = getContext().getAlias();
        if (!Strings.isNullOrEmpty(alias)) {
            getSqlString().append(" as ").append(alias);
        }
        return buildMetadata();
    }
}
