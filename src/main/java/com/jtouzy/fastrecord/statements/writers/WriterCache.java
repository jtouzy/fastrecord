package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.FastRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WriterCache {
    private final static Logger logger = LoggerFactory.getLogger(WriterCache.class);
    private Map<Class,Writer> writerCache = new HashMap<>();

    public WriterCache() {
    }

    @SuppressWarnings("unchecked")
    public <T> Writer<T> getWriter(T context) {
        Writer<T> writer = (Writer<T>)writerCache.get(context.getClass());
        if (writer == null) {
            logger.debug("No writer in cache.");
            writer = FastRecord.fr().getWriterFactory().getWriter(this, context);
            writerCache.put(context.getClass(), writer);
        } else {
            logger.debug("Get writer instance from cache [{}], updating context", writer);
            writer.setContext(context);
        }
        return writer;
    }
}
