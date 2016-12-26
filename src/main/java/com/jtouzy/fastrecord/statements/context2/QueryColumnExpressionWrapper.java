package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL query column expression (SELECT clause), wrapper with alias.
 */
public interface QueryColumnExpressionWrapper {
    /**
     * Get SQL query column alias.
     *
     * @return SQL query column alias
     */
    String getAlias();

    /**
     * Get SQL query column expression.
     *
     * @return SQL query column expression
     */
    QueryColumnExpression getExpression();
}
