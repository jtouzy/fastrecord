package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL query condition wrapper with operator.
 */
public interface QueryConditionWrapper extends ConditionWrapper, QueryConditionChain {
    /**
     * Get first SQL expression in condition.
     *
     * @return First SQL expression in condition
     */
    QueryConditionExpression getFirstConditionExpression();

    /**
     * Get compare SQL expression in condition.
     *
     * @return Compare SQL expression in condition
     */
    QueryConditionExpression getCompareConditionExpression();
}
