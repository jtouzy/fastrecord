package com.jtouzy.fastrecord.statements.context;

public interface ConstantContext extends ExpressionContext, TypedContext {
    String getValue();
}
