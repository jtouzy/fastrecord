package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.WritableContext;
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
    private final WriterPool writerPool;
    /**
     * Spring application context instance.
     * Injected bean.
     */
    private final ApplicationContext applicationContext;
    /**
     * Writers cache.
     * Each writer is stored here after getWriter() method is called for a given WritableContext class.
     */
    private final Map<Class<? extends WritableContext>,Writer> cache = new HashMap<>();

    /**
     * Constructor.
     * @param writerPool WriterPool instance
     * @param applicationContext Spring's ApplicationContext instance
     */
    @Autowired
    public WriterCache(WriterPool writerPool, ApplicationContext applicationContext) {
        this.writerPool = writerPool;
        this.applicationContext = applicationContext;
    }

    /**
     * Get a writer from cache or instantiate it with the Spring application context.
     *
     * If the writer is cacheable, store the instance for reuse it, otherwise create a new instance
     * each time the writer is called.
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
            if (writer.isCacheable()) {
                cache.put(writableContext.getClass(), writer);
            }
        }
        writer.refreshContext(writableContext, this);
        return writer;
    }
}
