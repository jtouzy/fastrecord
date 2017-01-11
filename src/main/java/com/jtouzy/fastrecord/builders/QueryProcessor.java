package com.jtouzy.fastrecord.builders;

import java.util.List;
import java.util.Optional;

public interface QueryProcessor<T> extends Processor {
    void init(Class<T> entityClass);

    QueryProcessor<T> fill(Class filledEntityClass);
    QueryProcessor<T> fill(Class filledEntityClass, String propertyName);
    QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass);
    QueryProcessor<T> fillFrom(Class originEntityClass, Class filledEntityClass, String propertyName);
    EntityBasedConditionsProcessor.ConditionsConfigurer conditions();
    QueryProcessor<T> orderBy(String columnName);
    QueryProcessor<T> orderBy(Class entityClass, String columnName);

    String getSql();

    Optional<T> findFirst();
    List<T> findAll();
}
