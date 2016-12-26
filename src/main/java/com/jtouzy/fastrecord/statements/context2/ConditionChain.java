package com.jtouzy.fastrecord.statements.context2;

import com.jtouzy.fastrecord.utils.Chain;

/**
 * SQL condition chain (in WHERE clause). Abstract generic interface.
 */
public interface ConditionChain {
    /**
     * Get the SQL condition chain.
     *
     * @return SQL condition chain.
     */
    Chain<ConditionChain,ConditionChainOperator> getChain();
}
