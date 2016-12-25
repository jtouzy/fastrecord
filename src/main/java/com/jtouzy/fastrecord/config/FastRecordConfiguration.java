package com.jtouzy.fastrecord.config;

public class FastRecordConfiguration {
    private static final String FASTRECORD_PROPERTIES_PREFIX = "fastrecord.";
    public static final String ENTITIES_CLASS_PACKAGE = FASTRECORD_PROPERTIES_PREFIX + "entities.package";
    public static final String WRITERS_CLASS_PACKAGE = FASTRECORD_PROPERTIES_PREFIX + "writers.package";
    public static final String TYPE_MANAGERS_CLASS_PACKAGE = FASTRECORD_PROPERTIES_PREFIX + "type.managers.package";

    private String entitiesClassPackage;
    private String writersClassPackage;
    private String typeManagersClassPackage;
    private NamingStrategy tableNamingStrategy;
    private NamingStrategy columnNamingStrategy;

    public FastRecordConfiguration() {
        setTableNamingStrategy(NamingStrategy.UPPER_CAMEL_TO_LOWER_UNDERSCORE);
        setColumnNamingStrategy(NamingStrategy.LOWER_CAMEL_TO_LOWER_UNDERSCORE);
    }

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

    public String getTypeManagersClassPackage() {
        return typeManagersClassPackage;
    }

    public void setTypeManagersClassPackage(String typeManagersClassPackage) {
        this.typeManagersClassPackage = typeManagersClassPackage;
    }
}
