package com.jtouzy.fastrecord.statements.context;

public interface TableColumnContext extends AliasExpressionContext, TypedContext {
    TableAliasContext getTableContext();
    String getColumn();
}
