package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.QueryExpression;

import java.util.List;
import java.util.Optional;

public interface QueryProcessor<T> extends ConditionsProcessor<T,QueryExpression> {
    QueryProcessor<T> fill(Class filledEntityClass);
    QueryProcessor<T> fill(Class filledEntityClass, String propertyName);
    QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass);
    QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass, String propertyName);
    QueryProcessor<T> orderBy(String columnName);
    QueryProcessor<T> orderBy(Class entityClass, String columnName);

    Optional<T> findFirst();
    List<T> findAll();

    // Overrides needed to return the good object type

    @Override QueryProcessor<T> chain();
    @Override QueryProcessor<T> and();
    @Override QueryProcessor<T> or();
    @Override QueryProcessor<T> end();

    @Override QueryProcessor<T> eq(String columnName, Object value);
    @Override QueryProcessor<T> notEq(String columnName, Object value);
    @Override QueryProcessor<T> like(String columnName, Object value);
    @Override QueryProcessor<T> notLike(String columnName, Object value);
    @Override QueryProcessor<T> in(String columnName, List<?> value);
    @Override QueryProcessor<T> notIn(String columnName, List<?> value);
    @Override QueryProcessor<T> andEq(String columnName, Object value);
    @Override QueryProcessor<T> andNotEq(String columnName, Object value);
    @Override QueryProcessor<T> andLike(String columnName, Object value);
    @Override QueryProcessor<T> andNotLike(String columnName, Object value);
    @Override QueryProcessor<T> andIn(String columnName, List<?> value);
    @Override QueryProcessor<T> andNotIn(String columnName, List<?> value);
    @Override QueryProcessor<T> orEq(String columnName, Object value);
    @Override QueryProcessor<T> orNotEq(String columnName, Object value);
    @Override QueryProcessor<T> orLike(String columnName, Object value);
    @Override QueryProcessor<T> orNotLike(String columnName, Object value);
    @Override QueryProcessor<T> orIn(String columnName, List<?> value);
    @Override QueryProcessor<T> orNotIn(String columnName, List<?> value);
}
