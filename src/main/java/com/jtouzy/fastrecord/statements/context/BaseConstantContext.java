package com.jtouzy.fastrecord.statements.context;

public class BaseConstantContext implements ConstantContext {
    private final String value;
    private final int type;

    public BaseConstantContext(String value, int type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }
}
