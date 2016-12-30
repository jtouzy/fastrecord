package com.jtouzy.fastrecord.statements.context;

import java.util.Map;

public interface UpdateExpression extends WriteWithConditionsExpression {
    Map<SimpleTableColumnExpression,UpdateValueExpression> getValues();
}
