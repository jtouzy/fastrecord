package com.jtouzy.fastrecord.statements.context2;

import com.jtouzy.fastrecord.utils.Chain;

/**
 * SQL condition chain (in WHERE clause). Abstract generic interface.
 */
public interface ConditionChain<T extends ConditionChain<T>> {
    /**
     * Add the first condition to the chain.
     *
     * @param conditionChain The condition chain to add to the chain
     */
    void addCondition(T conditionChain);

    /**
     * Add the non-first condition to the chain.
     *
     * @param operator Operator between the previous chain and the new added chain
     * @param conditionChain The condition chain to add to the chain
     */
    void addCondition(ConditionChainOperator operator, T conditionChain);

    /**
     * Get the SQL condition chain.
     *
     * @return SQL condition chain
     */
    Chain<T,ConditionChainOperator> getChain();
}
