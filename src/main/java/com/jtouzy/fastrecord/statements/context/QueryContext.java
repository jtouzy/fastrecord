package com.jtouzy.fastrecord.statements.context;

import java.util.List;

public interface QueryContext extends ExpressionContext, QueryFromContext {
    void addColumnContext(AliasExpressionContext expressionContext);
    void addFromContext(QueryFromContext fromContext);
    List<AliasExpressionContext> getColumnContextList();
    List<QueryFromContext> getFromContextList();
}
