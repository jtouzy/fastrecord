package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public FastRecordConfiguration configuration() {
        FastRecordConfiguration configuration = new FastRecordConfiguration();
        configuration.setEntitiesClassPackage("com.jtouzy.fastrecord");
        // TODO Load configuration and validate
        // if (configuration.getEntitiesClassPackage() == null) {
        // throw new ConfigurationException("Entities package must be provided");
        return configuration;
    }
}
