package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.statements.context2.WritableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Writer cache.
 * Each write process needs an instance of WriterCache to optimize Writers creation.
 * Each writer will be created only one time during a write process with this cache.
 *
 * @author jtouzy
 */
@Service("FastRecord.WriterProcess.WriterCache")
public class WriterCache {
    /**
     * Writer pool instance.
     * Injected bean.
     */
    @Autowired
    private WriterPool writerPool;
    /**
     * Spring application context instance.
     * Injected bean.
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * Writers cache.
     * Each writer is stored here after getWriter() method is called for a given WritableContext class.
     */
    private Map<Class<? extends WritableContext>,Writer> cache = new HashMap<>();

    /**
     * Get a writer from cache or instantiate it with the Spring application context.
     *
     * @param writableContext The writable context to send to the writer
     * @param <C> Type of the writable context
     *
     * @return The writer associated to the given writableContext
     */
    @SuppressWarnings("unchecked")
    public <C extends WritableContext> Writer<C> getWriter(C writableContext) {
        Writer<C> writer = cache.get(writableContext.getClass());
        if (writer == null) {
            Class<Writer> writerClass = writerPool.findWriterClassFor(writableContext.getClass());
            writer = applicationContext.getBean(writerClass);
            cache.put(writableContext.getClass(), writer);
        }
        writer.refreshContext(writableContext, this);
        return writer;
    }
}
