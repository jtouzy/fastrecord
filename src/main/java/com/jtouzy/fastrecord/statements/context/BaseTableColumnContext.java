package com.jtouzy.fastrecord.statements.context;

public class BaseTableColumnContext implements TableColumnContext {
    private final TableAliasContext tableContext;
    private final String column;
    private final int type;

    public BaseTableColumnContext(String tableAlias, String table, String column, int type) {
        this.tableContext = new BaseTableAliasContext(tableAlias, table);
        this.column = column;
        this.type = type;
    }

    @Override
    public TableAliasContext getTableContext() {
        return tableContext;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public int getType() {
        return type;
    }
}
