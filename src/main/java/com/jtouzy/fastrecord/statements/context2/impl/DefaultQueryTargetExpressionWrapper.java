package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.QueryTargetExpression;
import com.jtouzy.fastrecord.statements.context2.QueryTargetExpressionWrapper;

public class DefaultQueryTargetExpressionWrapper implements QueryTargetExpressionWrapper {
    private final String alias;
    private final QueryTargetExpression expression;

    public DefaultQueryTargetExpressionWrapper(String alias, QueryTargetExpression expression) {
        this.alias = alias;
        this.expression = expression;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public QueryTargetExpression getExpression() {
        return expression;
    }
}
