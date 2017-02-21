package com.jtouzy.fastrecord.statements.context;

import java.util.List;
import java.util.Map;

public interface InsertExpression extends WriteExpression {
    List<Map<SimpleTableColumnExpression, ConstantExpression>> getValues();
}
