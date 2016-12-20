package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.config.Configuration;
import com.jtouzy.fastrecord.config.ConfigurationException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.writers.WriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastRecord {
    private static final Logger logger = LoggerFactory.getLogger(FastRecord.class);
    private static FastRecord instance = null;

    private final EntityPool entityPool;
    private final WriterFactory writerFactory;

    private FastRecord(Configuration configuration) {
        validConfiguration(configuration);
        this.entityPool = EntityPool.init(configuration);
        this.writerFactory = WriterFactory.init(configuration);
    }

    public static FastRecord init() {
        checkInstance();
        logger.debug("Initializing FastRecord without configuration provided");
        return initialize(loadConfiguration());
    }

    public static FastRecord init(Configuration configuration) {
        checkInstance();
        logger.debug("Initializing FastRecord with configuration {}", configuration);
        return initialize(configuration);
    }

    public static FastRecord fr() {
        if (instance == null)
            throw new IllegalStateException("FastRecord is not initialized");
        return instance;
    }

    private static FastRecord initialize(Configuration configuration) {
        instance = new FastRecord(configuration);
        return instance;
    }

    private static void checkInstance() {
        if (instance != null)
            throw new IllegalStateException("FastRecord manager already initialized!");
    }

    private static Configuration loadConfiguration() {
        logger.debug("Load configuration...");
        return new Configuration();
    }

    private void validConfiguration(Configuration configuration) {
        if (configuration.getEntitiesClassPackage() == null) {
            throw new ConfigurationException("Entities package must be provided");
        }
    }

    public EntityPool getEntityPool() {
        return entityPool;
    }

    public WriterFactory getWriterFactory() {
        return writerFactory;
    }
}
