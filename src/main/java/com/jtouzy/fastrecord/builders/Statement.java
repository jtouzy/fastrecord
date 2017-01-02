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
    public <T> EntityQueryProcessor<T> queryFrom(Class<T> entityClass) {
        EntityQueryProcessor<T> entityQueryProcessor = applicationContext.getBean(EntityQueryProcessor.class);
        entityQueryProcessor.init(entityClass);
        return entityQueryProcessor;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityInsertProcessor<T> insert(T target) {
        return insert((Class<T>)target.getClass(), target);
    }

    @SuppressWarnings("unchecked")
    public <T> EntityInsertProcessor<T> insert(Class<T> entityClass, T target) {
        EntityInsertProcessor<T> entityWriteProcessor = applicationContext.getBean(EntityInsertProcessor.class);
        entityWriteProcessor.init(entityClass, target);
        return entityWriteProcessor;
    }
}
