package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL query target (in FROM clause) wrapper with alias.
 */
public interface QueryTargetExpressionWrapper extends WritableContext {
    String getAlias();
    QueryTargetExpression getExpression();
}
