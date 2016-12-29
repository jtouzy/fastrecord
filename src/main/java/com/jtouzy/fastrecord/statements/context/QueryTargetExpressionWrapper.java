package com.jtouzy.fastrecord.statements.context;

/**
 * SQL query target (in FROM clause) wrapper with alias.
 */
public interface QueryTargetExpressionWrapper extends WritableContext {
    String getAlias();
    QueryTargetExpression getExpression();
}
