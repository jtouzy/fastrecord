package com.jtouzy.fastrecord.statements.context2;

import java.util.List;

/**
 * SQL query expression.
 */
public interface QueryExpression
        extends NativeExpression, ConditionExpression, QueryColumnExpression, QueryTargetExpression {
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
     * Get the SQL condition chain (WHERE clause).
     *
     * @return SQL condition chain
     */
    QueryConditionChain getConditionChain();
}