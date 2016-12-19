package com.jtouzy.fastrecord.statements.context;

public interface TableColumnContext extends ExpressionContext, TypedContext {
    TableAliasContext getTableContext();
    String getColumn();
}
