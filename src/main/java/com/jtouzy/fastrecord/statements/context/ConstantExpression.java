package com.jtouzy.fastrecord.statements.context;

/**
 * SQL constant expression.
 */
public interface ConstantExpression
        extends NativeExpression, TypedExpression, QueryColumnExpression, ConditionExpression {
    /**
     * Get the SQL constant value.
     *
     * @return SQL constant value
     */
    String getValue();
}
