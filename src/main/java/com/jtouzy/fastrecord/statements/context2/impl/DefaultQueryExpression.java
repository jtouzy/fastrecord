package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultQueryExpression implements QueryExpression {
    private final List<QueryColumnExpressionWrapper> columns;
    private final QueryTargetExpressionWrapper mainTarget;
    private final List<QueryTargetExpressionJoin> joinList;
    private final QueryConditionChain conditionChain;

    public DefaultQueryExpression(QueryTargetExpressionWrapper mainTarget) {
        this.mainTarget = mainTarget;
        columns = new ArrayList<>();
        joinList = new ArrayList<>();
        conditionChain = createConditionChain();
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
}
