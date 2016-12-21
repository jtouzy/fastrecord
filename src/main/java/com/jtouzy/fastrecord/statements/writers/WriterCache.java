package com.jtouzy.fastrecord.statements.writers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class WriterCache {
    private final static Logger logger = LoggerFactory.getLogger(WriterCache.class);

    @Autowired
    private WriterFactory writerFactory;
    private Map<Class,Writer> writerCache = new HashMap<>();

    private WriterCache() {
    }

    @SuppressWarnings("unchecked")
    public <T> Writer<T> getWriter(T context) {
        Writer<T> writer = (Writer<T>)writerCache.get(context.getClass());
        if (writer == null) {
            logger.debug("No writer in cache.");
            writer = writerFactory.getWriter(this, context);
            writerCache.put(context.getClass(), writer);
        } else {
            logger.debug("Get writer instance from cache [{}], updating context", writer);
            writer.setContext(context);
        }
        return writer;
    }
}
