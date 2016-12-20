package com.jtouzy.fastrecord.config;

public class Configuration {
    private String entitiesClassPackage;
    private String writersClassPackage;
    private NamingStrategy tableNamingStrategy;
    private NamingStrategy columnNamingStrategy;

    public String getEntitiesClassPackage() {
        return entitiesClassPackage;
    }

    public void setEntitiesClassPackage(String entitiesClassPackage) {
        this.entitiesClassPackage = entitiesClassPackage;
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

    public String getWritersClassPackage() {
        return writersClassPackage;
    }

    public void setWritersClassPackage(String writersClassPackage) {
        this.writersClassPackage = writersClassPackage;
    }
}
