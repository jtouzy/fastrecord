package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.AliasConstantContext;
import com.jtouzy.fastrecord.statements.context.AliasQueryContext;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnContext;
import com.jtouzy.fastrecord.statements.context.QueryContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class WriterFactory {
    private static final Map<Class,Class<? extends Writer>> defaultWriters = new LinkedHashMap<>();
    private static final Map<Class,Class<? extends Writer>> writers = new LinkedHashMap<>();

    static {
        defaultWriters.put(QueryContext.class, DefaultQueryWriter.class);
        defaultWriters.put(AliasTableColumnContext.class, DefaultAliasTableColumnWriter.class);
        defaultWriters.put(AliasConstantContext.class, DefaultAliasConstantWriter.class);
        defaultWriters.put(AliasQueryContext.class, DefaultAliasQueryWriter.class);
    }

    private WriterFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <W extends Writer<T>,T> W getWriter(T context) {
        System.out.println("Factory : Getting new writer for context " + context + "...");
        Class<W> writerClass = (Class<W>)findWriterClass(context);
        Constructor<W> constructor = findConstructor(writerClass, context);
        try {
            W writer = constructor.newInstance(context);
            System.out.println("Writer found : " + writer);
            System.out.println("");
            return writer;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            throw new WriterDefinitionException("Error while calling " + writerClass.getClass() + " constructor", ex);
        }
    }

    private static <T> Class<? extends Writer<T>> findWriterClass(T context) {
        try {
            System.out.println("Search in writers...");
            return findInCollection(writers, context);
        } catch (WriterDefinitionException ex) {
            System.out.println("Search in default writers...");
            return findInCollection(defaultWriters, context);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends Writer<T>> findInCollection(Map<Class,Class<? extends Writer>> collection,
                                                                   T context) {
        Set<Class> contextClasses = collection.keySet();
        Class currentContextClass = context.getClass();
        while (true) {
            if (currentContextClass == null || currentContextClass == Object.class) {
                throw new WriterDefinitionException("No writer class found for " + context.getClass());
            }
            System.out.println("Search for " + currentContextClass);
            if (contextClasses.contains(currentContextClass)) {
                return (Class<? extends Writer<T>>)collection.get(currentContextClass);
            }
            for (Class interfaceClass : currentContextClass.getInterfaces()) {
                System.out.println("Search for " + interfaceClass);
                if (contextClasses.contains(interfaceClass)) {
                    return (Class<? extends Writer<T>>)collection.get(interfaceClass);
                }
            }
            currentContextClass = currentContextClass.getSuperclass();
        }
    }

    private static <W extends Writer<T>,T> Constructor<W> findConstructor(Class<W> writerClass, T context) {
        System.out.println("Search constructor...");
        Class currentContextClass = context.getClass();
        while (true) {
            if (currentContextClass == null || currentContextClass == Object.class) {
                throw new WriterDefinitionException(
                        "No constructor with context class found in " + writerClass.getClass());
            }
            System.out.println("Search constructor with " + currentContextClass);
            try {
                return writerClass.getConstructor(currentContextClass);
            } catch (NoSuchMethodException ex) {
                for (Class interfaceClass : currentContextClass.getInterfaces()) {
                    System.out.println("Search constructor with " + interfaceClass);
                    try {
                        return writerClass.getConstructor(interfaceClass);
                    } catch (NoSuchMethodException exi) {}
                }
                currentContextClass = currentContextClass.getSuperclass();
            }
        }
    }
}
