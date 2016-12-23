package com.jtouzy.fastrecord.statements.context;

public class BaseAliasQueryContext extends BaseQueryContext implements AliasQueryContext {
    private final String alias;

    public BaseAliasQueryContext(TableAliasContext mainTableContext, String alias) {
        super(mainTableContext);
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    // TODO REVIEW CLASS HIERARCHY

    @Override
    public String getTable() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return null;
    }
}
