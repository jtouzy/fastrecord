package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.QueryColumnExpression;
import com.jtouzy.fastrecord.statements.context.QueryColumnExpressionWrapper;

public class DefaultQueryColumnExpressionWrapper implements QueryColumnExpressionWrapper {
    private final String alias;
    private final QueryColumnExpression expression;

    public DefaultQueryColumnExpressionWrapper(String alias, QueryColumnExpression expression) {
        this.alias = alias;
        this.expression = expression;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public QueryColumnExpression getExpression() {
        return expression;
    }
}
