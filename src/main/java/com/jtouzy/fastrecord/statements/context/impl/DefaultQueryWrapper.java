package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.QueryWrapper;

public class DefaultQueryWrapper implements QueryWrapper {
    private final QueryExpression queryExpression;

    public DefaultQueryWrapper(QueryExpression queryExpression) {
        this.queryExpression = queryExpression;
    }

    @Override
    public QueryExpression getQueryExpression() {
        return queryExpression;
    }
}
