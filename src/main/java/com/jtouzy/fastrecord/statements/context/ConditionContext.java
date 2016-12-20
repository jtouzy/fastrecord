package com.jtouzy.fastrecord.statements.context;

import java.util.List;

public interface ConditionContext {
    void addFirstExpression(ExpressionContext expressionContext);
    List<ExpressionContext> getFirstExpressions();
    ConditionOperator getOperator();
    void addCompareExpression(ExpressionContext expressionContext);
    List<ExpressionContext> getCompareExpressions();
}
