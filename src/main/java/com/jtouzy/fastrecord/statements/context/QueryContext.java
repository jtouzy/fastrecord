package com.jtouzy.fastrecord.statements.context;

import com.jtouzy.fastrecord.utils.Chain;

import java.util.List;

public interface QueryContext extends ExpressionContext, QueryFromContext {
    void addColumnContext(AliasExpressionContext expressionContext);
    void addFromContext(QueryFromContext fromContext);
    void addFromContext(JoinOperator operator, QueryFromContext fromContext);
    List<AliasExpressionContext> getColumnContextList();
    Chain<QueryFromContext,JoinOperator> getFromContextChain();
}
