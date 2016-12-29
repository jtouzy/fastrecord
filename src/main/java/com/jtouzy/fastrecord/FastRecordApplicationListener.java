package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.entity.types.TypeManagerPool;
import com.jtouzy.fastrecord.statements.writers2.WriterPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component("FastRecord.Core.SpringApplicationListener")
public class FastRecordApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private TypeManagerPool typeManagerPool;
    @Autowired
    private EntityPool entityPool;
    @Autowired
    private WriterPool writerPool;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        ApplicationContext applicationContext = applicationEvent.getApplicationContext();
        typeManagerPool.initializeTypeManagers(applicationContext);
        entityPool.initializeEntities(applicationContext);
        writerPool.initializeWriters(applicationContext);
    }
}
