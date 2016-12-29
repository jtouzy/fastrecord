package com.jtouzy.fastrecord.statements.context;

/**
 * SQL join expression (in FROM clause).
 */
public interface QueryTargetExpressionJoin {
    /**
     * Get the first SQL expression in join.
     *
     * @return First SQL expression in context
     */
    QueryTargetExpressionWrapper getFirstTargetExpression();

    /**
     * Get join operator in join.
     *
     * @return Join operator in context
     */
    JoinOperator getJoinOperator();

    /**
     * Get the join target SQL expression in join.
     *
     * @return Join SQL expression in context
     */
    QueryTargetExpressionWrapper getJoinTargetExpression();
}
