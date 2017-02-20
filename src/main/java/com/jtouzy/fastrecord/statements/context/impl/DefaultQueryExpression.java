package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultQueryExpression implements QueryExpression {
    private final List<QueryColumnExpressionWrapper> columns;
    private final QueryTargetExpressionWrapper mainTarget;
    private final List<QueryTargetExpressionJoin> joinList;
    private final QueryConditionChain conditionChain;
    private final List<OrderByColumnWrapper> orderByList;

    public DefaultQueryExpression(QueryTargetExpressionWrapper mainTarget) {
        this.mainTarget = mainTarget;
        columns = new ArrayList<>();
        joinList = new ArrayList<>();
        conditionChain = createConditionChain();
        orderByList = new ArrayList<>();
    }

    protected QueryConditionChain createConditionChain() {
        return new DefaultQueryConditionChain();
    }

    @Override
    public List<QueryColumnExpressionWrapper> getColumns() {
        return columns;
    }

    @Override
    public QueryTargetExpressionWrapper getMainTargetExpression() {
        return mainTarget;
    }

    @Override
    public List<QueryTargetExpressionJoin> getTargetJoinList() {
        return joinList;
    }

    @Override
    public QueryConditionChain getConditionChain() {
        return conditionChain;
    }

    @Override
    public List<OrderByColumnWrapper> getOrderByColumns() {
        return orderByList;
    }
}
