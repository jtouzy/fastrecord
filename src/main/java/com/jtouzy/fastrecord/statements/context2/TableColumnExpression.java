package com.jtouzy.fastrecord.statements.context2;

/**
 * Native SQL table column expression.
 */
public interface TableColumnExpression<T extends TableExpression> extends NativeExpression, TypedExpression {
    /**
     * Get SQL table expression.
     *
     * @return SQL table expression
     */
    T getTableExpression();

    /**
     * Get SQL table column name.
     *
     * @return SQL table column name
     */
    String getColumn();
}
