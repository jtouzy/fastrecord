package com.jtouzy.fastrecord.statements.context;

import java.util.ArrayList;
import java.util.List;

public class BaseQueryContext implements QueryContext {
    private final List<AliasExpressionContext> columnContextList;
    private final List<QueryFromContext> fromContextList;

    public BaseQueryContext() {
        this.columnContextList = new ArrayList<>();
        this.fromContextList = new ArrayList<>();
    }

    @Override
    public List<AliasExpressionContext> getColumnContextList() {
        return columnContextList;
    }

    @Override
    public List<QueryFromContext> getFromContextList() {
        return fromContextList;
    }

    @Override
    public void addColumnContext(AliasExpressionContext expressionContext) {
        this.columnContextList.add(expressionContext);
    }

    @Override
    public void addFromContext(QueryFromContext fromContext) {
        this.fromContextList.add(fromContext);
    }
}
