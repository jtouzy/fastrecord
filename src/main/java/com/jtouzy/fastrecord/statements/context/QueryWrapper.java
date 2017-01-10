package com.jtouzy.fastrecord.statements.context;

public interface QueryWrapper extends ConditionExpression, QueryColumnExpression,
                                      QueryTargetExpression, UpdateValueExpression {
    QueryExpression getQueryExpression();
}
