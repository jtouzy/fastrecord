package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.builders.Statement;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class AbstractBuilderTest {
    @Autowired
    protected Statement statementProcessor;

    /**
     * Spring configuration for builders tests.
     */
    @Configuration
    @ComponentScan(basePackages = {
        "com.jtouzy.fastrecord.lifecycle",
        "com.jtouzy.fastrecord.statements.writers",
        "com.jtouzy.fastrecord.config",
        "com.jtouzy.fastrecord.entity",
        "com.jtouzy.fastrecord.builders",
        "com.jtouzy.fastrecord.tests.metadata.workingEntities",
    })
    public static class TestConfiguration {
    }
}
