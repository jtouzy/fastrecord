package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.WritableContext;

public interface ConditionsProcessor<T,E extends WritableContext> extends Processor<T,E> {
    ConditionsProcessor chain();
    ConditionsProcessor and();
    ConditionsProcessor or();
    ConditionsProcessor end();

    ConditionsProcessor eq(String columnName, Object value);
    ConditionsProcessor notEq(String columnName, Object value);
    ConditionsProcessor like(String columnName, Object value);
    ConditionsProcessor notLike(String columnName, Object value);
    ConditionsProcessor andEq(String columnName, Object value);
    ConditionsProcessor andNotEq(String columnName, Object value);
    ConditionsProcessor andLike(String columnName, Object value);
    ConditionsProcessor andNotLike(String columnName, Object value);
    ConditionsProcessor orEq(String columnName, Object value);
    ConditionsProcessor orNotEq(String columnName, Object value);
    ConditionsProcessor orLike(String columnName, Object value);
    ConditionsProcessor orNotLike(String columnName, Object value);
}
