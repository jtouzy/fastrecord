package com.jtouzy.fastrecord.statements.context2;

/**
 * SQL table column expression (without alias).
 */
public interface SimpleTableColumnExpression extends TableColumnExpression, UpdateConditionExpression {
    /**
     * Get SQL table expression.
     *
     * @return SQL table expression
     */
    SimpleTableExpression getTableExpression();
}
