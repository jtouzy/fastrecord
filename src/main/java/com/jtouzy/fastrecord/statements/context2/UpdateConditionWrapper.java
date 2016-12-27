package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL update (U,D) condition wrapper with operator.
 */
public interface UpdateConditionWrapper extends ConditionWrapper, UpdateConditionChain {
    /**
     * Get first SQL expression in condition.
     *
     * @return First SQL expression in condition
     */
    UpdateConditionExpression getFirstConditionExpression();

    /**
     * Get compare SQL expression in condition.
     *
     * @return Compare SQL expression in condition
     */
    UpdateConditionExpression getCompareConditionExpression();
}
