package com.jtouzy.fastrecord.statements.context;

public interface TableAliasContext extends TableContext, QueryFromContext {
    String getTableAlias();
}
