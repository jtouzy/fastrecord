package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.WritableContext;

import java.util.List;

public interface ConditionsProcessor<T,E extends WritableContext> extends Processor<T,E> {
    ConditionsProcessor chain();
    ConditionsProcessor and();
    ConditionsProcessor or();
    ConditionsProcessor end();

    ConditionsProcessor eq(String columnName, Object value);
    ConditionsProcessor notEq(String columnName, Object value);
    ConditionsProcessor like(String columnName, Object value);
    ConditionsProcessor notLike(String columnName, Object value);
    ConditionsProcessor in(String columnName, List<Object> values);
    ConditionsProcessor notIn(String columnName, List<Object> values);
    ConditionsProcessor andEq(String columnName, Object value);
    ConditionsProcessor andNotEq(String columnName, Object value);
    ConditionsProcessor andLike(String columnName, Object value);
    ConditionsProcessor andNotLike(String columnName, Object value);
    ConditionsProcessor andIn(String columnName, List<Object> values);
    ConditionsProcessor andNotIn(String columnName, List<Object> values);
    ConditionsProcessor orEq(String columnName, Object value);
    ConditionsProcessor orNotEq(String columnName, Object value);
    ConditionsProcessor orLike(String columnName, Object value);
    ConditionsProcessor orNotLike(String columnName, Object value);
    ConditionsProcessor orIn(String columnName, List<Object> values);
    ConditionsProcessor orNotIn(String columnName, List<Object> values);
}
