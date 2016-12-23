package com.jtouzy.fastrecord.statements.context;

public class BaseJoinContext implements JoinContext {
    private final TableAliasContext firstContext;
    private final JoinOperator joinOperator;
    private final TableAliasContext secondContext;

    public BaseJoinContext(TableAliasContext firstContext, JoinOperator joinOperator, TableAliasContext secondContext) {
        this.firstContext = firstContext;
        this.joinOperator = joinOperator;
        this.secondContext = secondContext;
    }

    @Override
    public TableAliasContext getFirstContext() {
        return firstContext;
    }

    @Override
    public JoinOperator getJoinOperator() {
        return joinOperator;
    }

    @Override
    public TableAliasContext getSecondContext() {
        return secondContext;
    }
}
