package com.jtouzy.fastrecord.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Statement {
    private final ApplicationContext applicationContext;

    @Autowired
    private Statement(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityBasedQuery<T> queryFrom(Class<T> entityClass) {
        EntityBasedQuery<T> entityBasedQuery = applicationContext.getBean(EntityBasedQuery.class);
        entityBasedQuery.fromClass(entityClass);
        return entityBasedQuery;
    }
}
