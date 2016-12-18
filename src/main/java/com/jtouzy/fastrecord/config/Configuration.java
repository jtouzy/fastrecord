package com.jtouzy.fastrecord.config;

public class Configuration {
    private String entitiesClassPath;
    private NamingStrategy tableNamingStrategy;
    private NamingStrategy columnNamingStrategy;

    public String getEntitiesClassPath() {
        return entitiesClassPath;
    }

    public void setEntitiesClassPath(String entitiesClassPath) {
        this.entitiesClassPath = entitiesClassPath;
    }

    public NamingStrategy getTableNamingStrategy() {
        return tableNamingStrategy;
    }

    public void setTableNamingStrategy(NamingStrategy tableNamingStrategy) {
        this.tableNamingStrategy = tableNamingStrategy;
    }

    public NamingStrategy getColumnNamingStrategy() {
        return columnNamingStrategy;
    }

    public void setColumnNamingStrategy(NamingStrategy columnNamingStrategy) {
        this.columnNamingStrategy = columnNamingStrategy;
    }
}
