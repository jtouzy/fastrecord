package com.jtouzy.fastrecord.statements.context;

public class BaseAliasQueryContext extends BaseQueryContext implements AliasQueryContext {
    private final String alias;

    public BaseAliasQueryContext(String alias) {
        super();
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
