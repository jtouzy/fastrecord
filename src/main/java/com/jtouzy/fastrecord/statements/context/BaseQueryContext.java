package com.jtouzy.fastrecord.statements.context;

import java.util.ArrayList;
import java.util.List;

public class BaseQueryContext implements QueryContext {
    private String alias;
    private final List<AliasExpressionContext> columnContextList;
    private final List<QueryFromContext> fromContextList;

    public BaseQueryContext() {
        this.columnContextList = new ArrayList<>();
        this.fromContextList = new ArrayList<>();
    }

    @Override
    public String getColumnAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
