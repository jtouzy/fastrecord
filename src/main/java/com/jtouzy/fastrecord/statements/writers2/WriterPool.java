package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.WritableContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service("FastRecord.Core.WriterPool")
public class WriterPool {
    private static final Logger logger = LoggerFactory.getLogger(WriterPool.class);
    private final Map<Class,Class> writerClassPool;
    private final Map<Class,String> writerBeanNameByClassMapping;

    public WriterPool() {
        writerClassPool = new LinkedHashMap<>();
        writerBeanNameByClassMapping = new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void initializeWriters(ApplicationContext applicationContext) {
        logger.debug("Registering default writers...");
        String[] writerBeanNames = applicationContext.getBeanNamesForAnnotation(Writes.class);
        Object unusedWriterInstance;
        Class writerClass, writableContextClass;
        for (String writerBeanName : writerBeanNames) {
            unusedWriterInstance = applicationContext.getBean(writerBeanName);
            writerClass = unusedWriterInstance.getClass();
            writableContextClass = ((Writes)writerClass.getAnnotation(Writes.class)).value();
            writerBeanNameByClassMapping.put(writerClass, writerBeanName);
            registerWriter(writableContextClass, writerClass);
        }
        logger.debug("End registering default writers.");
    }

    @SuppressWarnings("unchecked")
    <C extends WritableContext, T extends Writer<C>> Class<T> findWriterClassFor(Class<C> writableContextClass) {
        Class<T> writerClass = writerClassPool.get(writableContextClass);
        if (writerClass == null) {
            Class currentClass = writableContextClass;
            while (true) {
                if (currentClass == null | currentClass == Object.class) {
                    throw new WriterNotFoundException(writableContextClass);
                }
                writerClass = writerClassPool.get(currentClass);
                if (writerClass != null) {
                    registerWriter(writableContextClass, writerClass);
                    return writerClass;
                } else {
                    for (Class interfaceClass : currentClass.getInterfaces()) {
                        writerClass = writerClassPool.get(interfaceClass);
                        if (writerClass != null) {
                            registerWriter(writableContextClass, writerClass);
                            return writerClass;
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                }
            }
        }
        return writerClass;
    }

    private <C extends WritableContext, T extends Writer<C>> void registerWriter(Class<C> writableContextClass,
                                                                                 Class<T> writerClass) {
        logger.debug("Register writer [{}] for class [{}]",
                writerBeanNameByClassMapping.get(writerClass), writableContextClass);
        writerClassPool.put(writableContextClass, writerClass);
    }
}
