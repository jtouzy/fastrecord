package com.jtouzy.fastrecord.statements.context;

public interface OrderByColumnWrapper extends WritableContext {
    OrderByType getType();
    AliasTableColumnExpression getColumn();
}
