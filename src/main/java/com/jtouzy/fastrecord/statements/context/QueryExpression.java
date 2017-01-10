package com.jtouzy.fastrecord.statements.context;

import java.util.List;

/**
 * SQL query expression.
 */
public interface QueryExpression
        extends NativeExpression, ConditionChainHolder<QueryConditionChain> {
    /**
     * Get SQL query columns (SELECT clause).
     *
     * @return SQL query columns
     */
    List<QueryColumnExpressionWrapper> getColumns();

    /**
     * Get the SQL main target expression (FROM clause).
     *
     * @return SQL main target expression
     */
    QueryTargetExpressionWrapper getMainTargetExpression();

    /**
     * Get the SQL join list in target (FROM clause).
     *
     * @return SQL join list
     */
    List<QueryTargetExpressionJoin> getTargetJoinList();

    /**
     * Get the SQL query order by columns (ORDER BY)
     *
     * @return SQL query order by columns
     */
    List<AliasTableColumnExpression> getOrderByColumns();
}
