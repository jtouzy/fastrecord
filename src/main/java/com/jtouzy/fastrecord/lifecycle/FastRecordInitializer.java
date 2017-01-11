package com.jtouzy.fastrecord.lifecycle;

import com.jtouzy.fastrecord.builders.Statement;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.entity.types.TypeManagerPool;
import com.jtouzy.fastrecord.statements.writers.WriterPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("FastRecord.Core.FastRecordInitializer")
public class FastRecordInitializer {
    @Autowired
    private TypeManagerPool typeManagerPool;
    @Autowired
    private EntityPool entityPool;
    @Autowired
    private WriterPool writerPool;
    @Autowired
    private Statement processorsManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleApplicationRefreshed(ContextRefreshedEvent applicationEvent) {
        ApplicationContext applicationContext = applicationEvent.getApplicationContext();
        typeManagerPool.initializeTypeManagers(applicationContext);
        entityPool.initializeEntities(applicationContext);
        writerPool.initializeWriters(applicationContext);
        processorsManager.initializeProcessors();
        eventPublisher.publishEvent(new FastRecordInitializedEvent(this));
    }
}
