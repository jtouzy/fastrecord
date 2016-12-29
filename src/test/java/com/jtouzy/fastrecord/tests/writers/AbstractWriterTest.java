package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.Writer;
import com.jtouzy.fastrecord.statements.writers.WriterCache;
import com.jtouzy.fastrecord.statements.writers.WriterPool;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class AbstractWriterTest<C extends WritableContext, T extends Writer<C>> {
    private final Class<T> writerClass;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Spring configuration for writer tests.
     *
     * Only scan the writers package to avoid initialization of all other unwanted beans for this test.
     *
     * Simulation of FastRecordApplicationListener with only WriterPool initialization because the class
     * is not in ComponentScan scope for this test.
     */
    @Configuration
    @ComponentScan(basePackages = {"com.jtouzy.fastrecord.statements.writers"})
    public static class TestConfiguration implements ApplicationListener<ContextRefreshedEvent> {
        @Autowired
        private WriterPool writerPool;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            writerPool.initializeWriters(contextRefreshedEvent.getApplicationContext());
        }
    }

    public AbstractWriterTest(Class<T> writerClass) {
        this.writerClass = writerClass;
    }

    protected DbReadyStatementMetadata getWriterResult(C context)
    throws Exception {
        T writer = writerClass.newInstance();
        writer.refreshContext(context, applicationContext.getBean(WriterCache.class));
        writer.write();
        return writer.getResult();
    }
}
