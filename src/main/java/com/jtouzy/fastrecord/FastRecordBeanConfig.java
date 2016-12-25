package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static com.jtouzy.fastrecord.config.FastRecordConfiguration.*;

/**
 * FastRecord Spring Configuration
 * Registers all global beans
 *
 * @author jtouzy
 */
@Configuration
public class FastRecordBeanConfig {
    /**
     * Singleton FastRecordConfiguration Bean
     * This bean is used to store all the configuration properties for FastRecord.
     *
     * @return FastRecordConfiguration Bean
     */
    @Bean
    @Autowired
    public FastRecordConfiguration configuration(Environment environment) {
        FastRecordConfiguration configuration = new FastRecordConfiguration();
        configuration.setEntitiesClassPackage(environment.getRequiredProperty(ENTITIES_CLASS_PACKAGE));
        configuration.setWritersClassPackage(environment.getProperty(WRITERS_CLASS_PACKAGE));
        configuration.setTypeManagersClassPackage(environment.getProperty(TYPE_MANAGERS_CLASS_PACKAGE));
        return configuration;
    }
}
