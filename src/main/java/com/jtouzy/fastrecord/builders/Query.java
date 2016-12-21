package com.jtouzy.fastrecord.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Query {
    private static Query instance;

    @Autowired
    private ApplicationContext applicationContext;

    private Query() {
    }

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityBasedQuery<T> from(Class<T> entityClass) {
        EntityBasedQuery<T> entityBasedQuery = instance.applicationContext.getBean(EntityBasedQuery.class);
        entityBasedQuery.fromClass(entityClass);
        return entityBasedQuery;
    }

    private static <T> T getBean(Class<T> beanClass) {
        return instance.applicationContext.getBean(beanClass);
    }
}