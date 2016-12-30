package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.DeleteExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;

public class DefaultDeleteExpression extends AbstractWriteWithConditionsExpression implements DeleteExpression {
    public DefaultDeleteExpression(SimpleTableExpression target) {
        super(target);
    }
}
