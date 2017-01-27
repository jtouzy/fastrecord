package com.jtouzy.fastrecord.statements.context;

/**
 * Native SQL table expression.
 */
public interface TableExpression extends NativeExpression {
    /**
     * Get SQL schema of the table.
     *
     * @return SQL schema name
     */
    String getSchema();
    /**
     * Get SQL table name.
     *
     * @return SQL table name
     */
    String getTable();
}
