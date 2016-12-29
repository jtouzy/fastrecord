package com.jtouzy.fastrecord.statements.context;

/**
 * SQL table with alias expression.
 */
public interface AliasTableExpression extends TableExpression {
    /**
     * Get SQL table alias.
     *
     * @return SQL table alias
     */
    String getTableAlias();
}
