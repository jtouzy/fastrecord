package com.jtouzy.fastrecord.statements.context2;

/**
 * Native SQL table expression.
 */
public interface TableExpression extends NativeExpression {
    /**
     * Get SQL table name.
     *
     * @return SQL table name
     */
    String getTable();
}
