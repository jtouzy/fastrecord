package com.jtouzy.fastrecord.statements.context;

import java.util.Map;

public interface InsertExpression extends WriteExpression {
    Map<SimpleTableColumnExpression, ConstantExpression> getValues();
}
