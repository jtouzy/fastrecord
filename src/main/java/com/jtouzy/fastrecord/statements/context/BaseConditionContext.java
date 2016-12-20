package com.jtouzy.fastrecord.statements.context;

import java.util.ArrayList;
import java.util.List;

public class BaseConditionContext implements ConditionContext {
    private final List<ExpressionContext> firstExpressions;
    private final ConditionOperator operator;
    private final List<ExpressionContext> compareExpressions;

    public BaseConditionContext(ConditionOperator operator) {
        this.operator = operator;
        this.firstExpressions = new ArrayList<>();
        this.compareExpressions = new ArrayList<>();
    }

    @Override
    public void addFirstExpression(ExpressionContext expressionContext) {
        this.firstExpressions.add(expressionContext);
    }

    @Override
    public void addCompareExpression(ExpressionContext expressionContext) {
        this.compareExpressions.add(expressionContext);
    }

    @Override
    public List<ExpressionContext> getFirstExpressions() {
        return firstExpressions;
    }

    @Override
    public ConditionOperator getOperator() {
        return operator;
    }

    @Override
    public List<ExpressionContext> getCompareExpressions() {
        return compareExpressions;
    }
}
