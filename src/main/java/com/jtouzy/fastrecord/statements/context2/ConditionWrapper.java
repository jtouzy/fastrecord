package com.jtouzy.fastrecord.statements.context2;

import java.util.List;

/**
 * SQL condition wrapper with operator.
 */
public interface ConditionWrapper<E extends BasicConditionExpression> extends WritableContext {
    /**
     * Get first SQL expressions in condition.
     *
     * @return First SQL expressions in condition
     */
    List<E> getFirstConditionExpressions();

    /**
     * Get SQL condition operator.
     *
     * @return SQL condition operator
     */
    ConditionOperator getConditionOperator();

    /**
     * Get compare SQL expressions in condition.
     *
     * @return Compare SQL expressions in condition
     */
    List<E> getCompareConditionExpressions();
}
