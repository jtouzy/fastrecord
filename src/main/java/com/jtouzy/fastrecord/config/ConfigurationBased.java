package com.jtouzy.fastrecord.config;

public class ConfigurationBased {
    private final FastRecordConfiguration configuration;

    public ConfigurationBased(FastRecordConfiguration configuration) {
        this.configuration = configuration;
    }

    public FastRecordConfiguration getConfiguration() {
        return configuration;
    }
}
