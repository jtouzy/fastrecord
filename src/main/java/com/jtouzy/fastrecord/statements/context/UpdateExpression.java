package com.jtouzy.fastrecord.statements.context;

import java.util.Map;

public interface UpdateExpression extends WriteExpression {
    Map<SimpleTableColumnExpression,UpdateValueExpression> getValues();
    UpdateConditionChain getConditionChain();
}
