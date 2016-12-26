package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL aliased table column expression.
 */
public interface AliasTableColumnExpression
        extends TableColumnExpression, QueryColumnExpression, QueryConditionExpression {
    /**
     * Get SQL aliased table expression.
     *
     * @return SQL aliased table expression
     */
    AliasTableExpression getTableExpression();
}
