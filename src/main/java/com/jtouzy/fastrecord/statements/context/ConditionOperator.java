package com.jtouzy.fastrecord.statements.context;

public enum ConditionOperator {
    EQUALS,
    NOT_EQUALS,
    IN,
    NOT_IN,
    LIKE,
    NOT_LIKE,
    EXISTS,
    NOT_EXISTS;

    public boolean hasFirstExpression() {
        return !this.equals(EXISTS) && !this.equals(NOT_EXISTS);
    }
}
