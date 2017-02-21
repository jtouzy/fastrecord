package com.jtouzy.fastrecord.builders;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.fastrecord.annotations.support.Process;
import com.jtouzy.fastrecord.statements.context.DeleteExpression;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.utils.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Statement {
    private static final Logger logger = LoggerFactory.getLogger(Statement.class);
    private final ApplicationContext applicationContext;
    // TODO separate WriteProcessors and QueryProcessors to auto-check types at compile and avoid dirty casting
    private final Map<Class<? extends WritableContext>,Class<? extends Processor>> processorsByClass;

    @Inject
    public Statement(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.processorsByClass = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void initializeProcessors() {
        logger.debug("Registering default processors...");
        String[] processorBeanNames = applicationContext.getBeanNamesForAnnotation(Process.class);
        Object unusedProcessorInstance;
        Class processorClass, writableContextClass;
        Multimap<Class<? extends WritableContext>,Class<? extends Processor>> writableContextProcessors =
                ArrayListMultimap.create();
        Map<Class<? extends Processor>,Integer> prioritiesByProcessor = new HashMap<>();
        Process annotation;
        for (String processorBeanName : processorBeanNames) {
            unusedProcessorInstance = applicationContext.getBean(processorBeanName);
            processorClass = unusedProcessorInstance.getClass();
            annotation = (Process) processorClass.getAnnotation(Process.class);
            writableContextClass = annotation.value();
            writableContextProcessors.put(writableContextClass, processorClass);
            prioritiesByProcessor.put(processorClass, annotation.priority());
        }
        processorsByClass.putAll(Priority.getPriorityMap(writableContextProcessors, prioritiesByProcessor));
        logger.debug("End registering default processors.");
    }

    @SuppressWarnings("unchecked")
    public <T> QueryProcessor<T> queryFrom(Class<T> entityClass) {
        Class<? extends QueryProcessor<T>> processorClass =
                (Class<? extends QueryProcessor<T>>)processorsByClass.get(QueryExpression.class);
        QueryProcessor<T> processor = applicationContext.getBean(processorClass);
        processor.initProcessor(entityClass);
        return processor;
    }

    @SuppressWarnings("unchecked")
    public <T> InsertProcessor<T> insert(T target) {
        return insert((Class<T>)target.getClass(), target);
    }

    @SuppressWarnings("unchecked")
    public <T> InsertProcessor<T> insert(Class<T> entityClass, T target) {
        Class<? extends InsertProcessor<T>> processorClass =
                (Class<? extends InsertProcessor<T>>)processorsByClass.get(InsertExpression.class);
        InsertProcessor<T> processor = applicationContext.getBean(processorClass);
        processor.initWriteProcessor(entityClass, target);
        return processor;
    }

    @SuppressWarnings("unchecked")
    public <T> InsertProcessor<T> insert(Class<T> entityClass, List<T> targetList) {
        Class<? extends InsertProcessor<T>> processorClass =
                (Class<? extends InsertProcessor<T>>)processorsByClass.get(InsertExpression.class);
        InsertProcessor<T> processor = applicationContext.getBean(processorClass);
        processor.initProcessor(entityClass, targetList);
        return processor;
    }

    @SuppressWarnings("unchecked")
    public <T> UpdateProcessor<T> update(T target) {
        return update((Class<T>)target.getClass(), target);
    }

    @SuppressWarnings("unchecked")
    public <T> UpdateProcessor<T> update(Class<T> entityClass, T target) {
        Class<? extends UpdateProcessor<T>> processorClass =
                (Class<? extends UpdateProcessor<T>>)processorsByClass.get(UpdateExpression.class);
        UpdateProcessor<T> processor = applicationContext.getBean(processorClass);
        processor.initWriteProcessor(entityClass, target);
        return processor;
    }

    @SuppressWarnings("unchecked")
    public <T> DeleteProcessor<T> delete(T target) {
        return delete((Class<T>)target.getClass(), target);
    }

    @SuppressWarnings("unchecked")
    public <T> DeleteProcessor<T> delete(Class<T> entityClass, T target) {
        Class<? extends DeleteProcessor<T>> processorClass =
                (Class<? extends DeleteProcessor<T>>)processorsByClass.get(DeleteExpression.class);
        DeleteProcessor<T> processor = applicationContext.getBean(processorClass);
        processor.initWriteProcessor(entityClass, target);
        return processor;
    }
}
