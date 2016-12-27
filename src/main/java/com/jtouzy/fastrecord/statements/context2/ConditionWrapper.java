package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL condition wrapper with operator.
 */
public interface ConditionWrapper<E extends BasicConditionExpression> {
    /**
     * Get first SQL expression in condition.
     *
     * @return First SQL expression in condition
     */
    E getFirstConditionExpression();

    /**
     * Get SQL condition operator.
     *
     * @return SQL condition operator
     */
    ConditionOperator getConditionOperator();

    /**
     * Get compare SQL expression in condition.
     *
     * @return Compare SQL expression in condition
     */
    E getCompareConditionExpression();
}
