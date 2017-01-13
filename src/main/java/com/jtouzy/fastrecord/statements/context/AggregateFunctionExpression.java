package com.jtouzy.fastrecord.statements.context;

public interface AggregateFunctionExpression extends QueryColumnExpression {
    AggregateFunctionType getType();
    AliasTableColumnExpression getColumn();
}
