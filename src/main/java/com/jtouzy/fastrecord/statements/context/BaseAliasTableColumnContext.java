package com.jtouzy.fastrecord.statements.context;

public class BaseAliasTableColumnContext extends BaseTableColumnContext implements AliasTableColumnContext {
    private final String alias;

    public BaseAliasTableColumnContext(String alias, String tableAlias, String table, String column, int type) {
        super(tableAlias, table, column, type);
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
