package com.jtouzy.fastrecord.statements.context;

import com.jtouzy.fastrecord.utils.Chain;

import java.util.ArrayList;
import java.util.List;

public class BaseQueryContext implements QueryContext {
    private final List<AliasExpressionContext> columnContextList;
    private final Chain<QueryFromContext,JoinOperator> fromContextChain;

    public BaseQueryContext() {
        this.columnContextList = new ArrayList<>();
        this.fromContextChain = new Chain<>();
    }

    @Override
    public List<AliasExpressionContext> getColumnContextList() {
        return columnContextList;
    }

    @Override
    public Chain<QueryFromContext,JoinOperator> getFromContextChain() {
        return fromContextChain;
    }

    @Override
    public void addColumnContext(AliasExpressionContext expressionContext) {
        this.columnContextList.add(expressionContext);
    }

    @Override
    public void addFromContext(QueryFromContext fromContext) {
        this.fromContextChain.addFirst(fromContext);
    }

    @Override
    public void addFromContext(JoinOperator operator, QueryFromContext fromContext) {
        this.fromContextChain.add(operator, fromContext);
    }
}
