package com.jtouzy.fastrecord.config;

public class ConfigurationBased {
    private final Configuration configuration;

    public ConfigurationBased(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
