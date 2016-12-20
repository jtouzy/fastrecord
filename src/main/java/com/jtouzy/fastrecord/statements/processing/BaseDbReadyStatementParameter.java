package com.jtouzy.fastrecord.statements.processing;

public class BaseDbReadyStatementParameter implements DbReadyStatementParameter {
    private final String value;
    private final int type;

    public BaseDbReadyStatementParameter(String value, int type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public int getType() {
        return type;
    }
}
