package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryTargetExpressionJoin;
import com.jtouzy.fastrecord.statements.context.QueryTargetExpressionWrapper;

public class DefaultQueryTargetExpressionJoin implements QueryTargetExpressionJoin {
    private final QueryTargetExpressionWrapper firstTargetExpression;
    private final JoinOperator joinOperator;
    private final QueryTargetExpressionWrapper joinTargetExpression;

    public DefaultQueryTargetExpressionJoin(QueryTargetExpressionWrapper firstTargetExpression,
                                            JoinOperator joinOperator,
                                            QueryTargetExpressionWrapper joinTargetExpression) {
        this.firstTargetExpression = firstTargetExpression;
        this.joinOperator = joinOperator;
        this.joinTargetExpression = joinTargetExpression;
    }

    @Override
    public QueryTargetExpressionWrapper getFirstTargetExpression() {
        return firstTargetExpression;
    }

    @Override
    public JoinOperator getJoinOperator() {
        return joinOperator;
    }

    @Override
    public QueryTargetExpressionWrapper getJoinTargetExpression() {
        return joinTargetExpression;
    }
}
