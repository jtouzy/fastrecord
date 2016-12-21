package com.jtouzy.fastrecord.statements.writers;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.config.ConfigurationBased;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton WriterFactory Bean
 * This bean is used to create all the writers needed to create a full SQLStatement.
 *
 * @author jtouzy
 */
@Service
public class WriterFactory extends ConfigurationBased {
    private static final Logger logger = LoggerFactory.getLogger(WriterFactory.class);
    private static final String DEFAULT_WRITERS_CLASS_PACKAGE = "com.jtouzy.fastrecord.statements.writers";

    private Map<Class,Class<? extends Writer>> defaultWriters = new LinkedHashMap<>();
    private Map<Class,Class<? extends Writer>> writers = new LinkedHashMap<>();
    private Map<Class,Constructor<? extends Writer>> writersCache = new LinkedHashMap<>();

    @Autowired
    private WriterFactory(FastRecordConfiguration configuration) {
        super(configuration);
        this.initializeWriters();
    }

    private void initializeWriters() {
        putInCollection(DEFAULT_WRITERS_CLASS_PACKAGE, defaultWriters);
        putInCollection(getConfiguration().getWritersClassPackage(), writers);
    }

    @SuppressWarnings("unchecked")
    private void putInCollection(String classPackage, Map<Class,Class<? extends Writer>> collection) {
        if (Strings.isNullOrEmpty(classPackage)) {
            return;
        }
        Reflections reflections = new Reflections(classPackage);
        Set<Class<?>> writerClasses = reflections.getTypesAnnotatedWith(Writes.class);
        for (Class<?> writerClass : writerClasses) {
            Class contextClass = (writerClass.getAnnotation(Writes.class)).value();
            if (Writer.class.isAssignableFrom(writerClass)) {
                defaultWriters.put(contextClass, (Class<? extends Writer>)writerClass);
            } else {
                throw new WriterDefinitionException("@Writes annotated class " + contextClass
                        + " must implement Writer interface");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <W extends Writer<T>,T> W getWriter(WriterCache writerCache, T context) {
        logger.debug("Getting new writer from Factory for context {}...", context);
        Constructor<W> constructor = (Constructor<W>)writersCache.get(context.getClass());
        if (constructor != null) {
            logger.debug("Get constructor from cache");
        } else {
            Class<W> writerClass = (Class<W>) findWriterClass(context);
            constructor = findConstructor(writerClass, context);
        }
        try {
            W writer = constructor.newInstance(writerCache, context);
            writersCache.putIfAbsent(context.getClass(), constructor);
            logger.debug("Writer found {}", writer);
            return writer;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            throw new WriterDefinitionException("Error while calling " + constructor.getDeclaringClass() + " constructor", ex);
        }
    }

    private <T> Class<? extends Writer<T>> findWriterClass(T context) {
        try {
            logger.debug("Search in writers...");
            return findInCollection(writers, context);
        } catch (WriterDefinitionException ex) {
            logger.debug("Search in default writers...");
            return findInCollection(defaultWriters, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends Writer<T>> findInCollection(Map<Class,Class<? extends Writer>> collection,
                                                            T context) {
        Set<Class> contextClasses = collection.keySet();
        Class currentContextClass = context.getClass();
        while (true) {
            if (currentContextClass == null || currentContextClass == Object.class) {
                throw new WriterDefinitionException("No writer class found for " + context);
            }
            logger.debug("Search writer for {}", currentContextClass);
            if (contextClasses.contains(currentContextClass)) {
                return (Class<? extends Writer<T>>)collection.get(currentContextClass);
            }
            for (Class interfaceClass : currentContextClass.getInterfaces()) {
                logger.debug("Search writer for {}", interfaceClass);
                if (contextClasses.contains(interfaceClass)) {
                    return (Class<? extends Writer<T>>)collection.get(interfaceClass);
                }
            }
            currentContextClass = currentContextClass.getSuperclass();
        }
    }

    private <W extends Writer<T>,T> Constructor<W> findConstructor(Class<W> writerClass, T context) {
        logger.debug("Search constructor for class {}", writerClass);
        Class currentContextClass = context.getClass();
        while (true) {
            if (currentContextClass == null || currentContextClass == Object.class) {
                throw new WriterDefinitionException(
                        "No constructor with context class found in " + writerClass);
            }
            logger.debug("Search constructor with parameter type {}", currentContextClass);
            try {
                return writerClass.getConstructor(WriterCache.class, currentContextClass);
            } catch (NoSuchMethodException ex) {
                for (Class interfaceClass : currentContextClass.getInterfaces()) {
                    logger.debug("Search constructor with parameter type {}", interfaceClass);
                    try {
                        return writerClass.getConstructor(WriterCache.class, interfaceClass);
                    } catch (NoSuchMethodException exi) {}
                }
                currentContextClass = currentContextClass.getSuperclass();
            }
        }
    }
}
