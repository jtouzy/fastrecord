package com.jtouzy.fastrecord.statements.context2;

import com.jtouzy.fastrecord.utils.Chain;

/**
 * SQL update (U,D) condition chain (in WHERE clause).
 */
public interface UpdateConditionChain extends ConditionChain {
    /**
     * Get the SQL condition chain.
     *
     * @return SQL condition chain.
     */
    Chain<UpdateConditionChain, ConditionChainOperator> getChain();
}
