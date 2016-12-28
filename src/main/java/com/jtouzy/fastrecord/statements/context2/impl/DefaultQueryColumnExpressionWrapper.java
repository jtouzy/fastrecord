package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.QueryColumnExpression;
import com.jtouzy.fastrecord.statements.context2.QueryColumnExpressionWrapper;

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
