package com.jtouzy.fastrecord.statements.context;

import java.util.List;

public interface QueryContext extends ExpressionContext {
    void addColumnContext(AliasExpressionContext expressionContext);
    List<AliasExpressionContext> getColumnContextList();
    JoinListContext getJoinListContext();
    ConditionsContext getConditionsContext();
}
