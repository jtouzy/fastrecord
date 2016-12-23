package com.jtouzy.fastrecord.statements.context;

public interface JoinContext {
    TableAliasContext getFirstContext();
    JoinOperator getJoinOperator();
    TableAliasContext getSecondContext();
}
