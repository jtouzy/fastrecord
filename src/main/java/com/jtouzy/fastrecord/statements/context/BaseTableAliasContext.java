package com.jtouzy.fastrecord.statements.context;

public class BaseTableAliasContext implements TableAliasContext {
    private final String tableAlias;
    private final String table;

    public BaseTableAliasContext(String tableAlias, String table) {
        this.tableAlias = tableAlias;
        this.table = table;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public String getTable() {
        return table;
    }
}
