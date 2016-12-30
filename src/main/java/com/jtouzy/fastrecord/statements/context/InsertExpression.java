package com.jtouzy.fastrecord.statements.context;

import java.util.Map;

public interface InsertExpression extends WritableContext {
    SimpleTableExpression getTarget();
    Map<SimpleTableColumnExpression,String> getValues();
}
