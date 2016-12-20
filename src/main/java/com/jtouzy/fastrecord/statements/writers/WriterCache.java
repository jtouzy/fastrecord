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

    public Writer getWriter(Object context) {
        Writer writer = writerCache.get(context.getClass());
        if (writer == null) {
            logger.debug("No writer in cache.");
            writer = FastRecord.fr().getWriterFactory().getWriter(this, context);
            writerCache.put(context.getClass(), writer);
        } else {
            logger.debug("Get writer instance from cache [{}]", writer);
        }
        return writer;
    }
}
