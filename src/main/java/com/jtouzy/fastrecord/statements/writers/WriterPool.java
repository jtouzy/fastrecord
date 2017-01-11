package com.jtouzy.fastrecord.statements.writers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.utils.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service("FastRecord.Core.WriterPool")
public class WriterPool {
    private static final Logger logger = LoggerFactory.getLogger(WriterPool.class);
    private final Map<Class<? extends WritableContext>,Class<? extends Writer>> writerClassPool;

    public WriterPool() {
        writerClassPool = new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void initializeWriters(ApplicationContext applicationContext) {
        logger.debug("Registering default writers...");
        String[] writerBeanNames = applicationContext.getBeanNamesForAnnotation(Writes.class);
        Object unusedWriterInstance;
        Class writerClass, writableContextClass;
        Multimap<Class<? extends WritableContext>,Class<? extends Writer>> writableContextWriters =
                ArrayListMultimap.create();
        Map<Class<? extends Writer>,Integer> prioritiesByWriter = new HashMap<>();
        Writes annotation;
        for (String writerBeanName : writerBeanNames) {
            unusedWriterInstance = applicationContext.getBean(writerBeanName);
            writerClass = unusedWriterInstance.getClass();
            annotation = (Writes)writerClass.getAnnotation(Writes.class);
            writableContextClass = annotation.value();
            writableContextWriters.put(writableContextClass, writerClass);
            prioritiesByWriter.put(writerClass, annotation.priority());
        }
        registerWriters(Priority.getPriorityMap(writableContextWriters, prioritiesByWriter));
        logger.debug("End registering default writers.");
    }

    Class<? extends Writer> findWriterClassFor(Class<? extends WritableContext> writableContextClass) {
        Class<? extends Writer> writerClass = writerClassPool.get(writableContextClass);
        if (writerClass == null) {
            Class currentClass = writableContextClass;
            while (true) {
                if (currentClass == null | currentClass == Object.class) {
                    throw new WriterNotFoundException(writableContextClass);
                }
                writerClass = writerClassPool.get(currentClass);
                if (writerClass == null) {
                    for (Class interfaceClass : currentClass.getInterfaces()) {
                        writerClass = writerClassPool.get(interfaceClass);
                        if (writerClass != null) {
                            registerWriter(writableContextClass, writerClass);
                            return writerClass;
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                } else {
                    registerWriter(writableContextClass, writerClass);
                    return writerClass;
                }
            }
        }
        return writerClass;
    }

    private void registerWriters(Map<Class<? extends WritableContext>,Class<? extends Writer>> writersMap) {
        if (logger.isDebugEnabled()) {
            writersMap.entrySet().forEach(e ->
                    logger.debug("Register writer [{}] for class [{}]", e.getValue(), e.getKey()));
        }
        writerClassPool.putAll(writersMap);
    }

    private <C extends WritableContext, T extends Writer<C>> void registerWriter(Class<C> writableContextClass,
                                                                                 Class<T> writerClass) {
        logger.debug("Register writer [{}] for class [{}]", writerClass, writableContextClass);
        writerClassPool.put(writableContextClass, writerClass);
    }
}
