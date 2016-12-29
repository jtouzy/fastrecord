package com.jtouzy.fastrecord.statements.context;

/**
 * SQL typed expression.
 */
public interface TypedExpression {
    /**
     * Get SQL type.
     *
     * @return SQL type
     */
    int getType();
}
