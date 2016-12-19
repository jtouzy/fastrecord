package com.jtouzy.fastrecord.statements.processing;

public class BaseDbReadyStatementMetadata implements DbReadyStatementMetadata {
    private final String sqlString;

    public BaseDbReadyStatementMetadata(String sqlString) {
        this.sqlString = sqlString;
    }

    @Override
    public String getSqlString() {
        return this.sqlString;
    }
}
