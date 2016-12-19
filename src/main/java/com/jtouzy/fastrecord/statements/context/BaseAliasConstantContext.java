package com.jtouzy.fastrecord.statements.context;

public class BaseAliasConstantContext extends BaseConstantContext implements AliasConstantContext {
    private final String alias;

    public BaseAliasConstantContext(String alias, String value, int type) {
        super(value, type);
        this.alias = alias;
    }

    @Override
    public String getColumnAlias() {
        return alias;
    }
}
