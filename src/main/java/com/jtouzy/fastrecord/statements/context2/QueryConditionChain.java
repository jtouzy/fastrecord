package com.jtouzy.fastrecord.statements.context2;

import com.jtouzy.fastrecord.utils.Chain;

/**
 * SQL query condition chain (in WHERE clause).
 */
public interface QueryConditionChain extends ConditionChain {
    /**
     * Get the SQL query condition chain.
     *
     * @return SQL query condition chain.
     */
    Chain<QueryConditionChain, ConditionChainOperator> getChain();
}
