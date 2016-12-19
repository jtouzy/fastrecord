package com.jtouzy.fastrecord.statements.context;

import java.util.List;

public interface QueryContext extends AliasExpressionContext, QueryFromContext {
    void addColumnContext(AliasExpressionContext expressionContext);
    void addFromContext(QueryFromContext fromContext);
    List<AliasExpressionContext> getColumnContextList();
    List<QueryFromContext> getFromContextList();
}
