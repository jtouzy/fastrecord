package com.jtouzy.fastrecord.lifecycle;

import com.jtouzy.fastrecord.builders.Statement;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.entity.types.TypeManagerPool;
import com.jtouzy.fastrecord.statements.writers.WriterPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("FastRecord.Core.FastRecordInitializer")
public class FastRecordInitializer {
    private final TypeManagerPool typeManagerPool;
    private final EntityPool entityPool;
    private final WriterPool writerPool;
    private final Statement processorsManager;
    private final ApplicationEventPublisher eventPublisher;

    @Inject
    public FastRecordInitializer(TypeManagerPool typeManagerPool, EntityPool entityPool, WriterPool writerPool,
                                 Statement processorsManager, ApplicationEventPublisher eventPublisher) {
        this.typeManagerPool = typeManagerPool;
        this.entityPool = entityPool;
        this.writerPool = writerPool;
        this.processorsManager = processorsManager;
        this.eventPublisher = eventPublisher;
    }

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
