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

    Integer count();
    Integer count(String columnName);
    Optional<T> findFirst();
    List<T> findAll();

    // Overrides needed to return the good object type

    @Override QueryProcessor<T> chain();
    @Override QueryProcessor<T> and();
    @Override QueryProcessor<T> or();
    @Override QueryProcessor<T> end();

    QueryProcessor<T> eq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> notEq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> like(Class entityClass, String columnName, Object value);
    QueryProcessor<T> notLike(Class entityClass, String columnName, Object value);
    QueryProcessor<T> in(Class entityClass, String columnName, List<?> value);
    QueryProcessor<T> notIn(Class entityClass, String columnName, List<?> value);
    QueryProcessor<T> andEq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> andNotEq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> andLike(Class entityClass, String columnName, Object value);
    QueryProcessor<T> andNotLike(Class entityClass, String columnName, Object value);
    QueryProcessor<T> andIn(Class entityClass, String columnName, List<?> value);
    QueryProcessor<T> andNotIn(Class entityClass, String columnName, List<?> value);
    QueryProcessor<T> orEq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> orNotEq(Class entityClass, String columnName, Object value);
    QueryProcessor<T> orLike(Class entityClass, String columnName, Object value);
    QueryProcessor<T> orNotLike(Class entityClass, String columnName, Object value);
    QueryProcessor<T> orIn(Class entityClass, String columnName, List<?> value);
    QueryProcessor<T> orNotIn(Class entityClass, String columnName, List<?> value);

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

    @Override QueryProcessor<T> exists(ConditionsProcessor conditionsProcessor);
    @Override QueryProcessor<T> andExists(ConditionsProcessor conditionsProcessor);
    @Override QueryProcessor<T> orExists(ConditionsProcessor conditionsProcessor);
    @Override QueryProcessor<T> notExists(ConditionsProcessor conditionsProcessor);
    @Override QueryProcessor<T> andNotExists(ConditionsProcessor conditionsProcessor);
    @Override QueryProcessor<T> orNotExists(ConditionsProcessor conditionsProcessor);
}
