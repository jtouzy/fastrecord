package com.jtouzy.fastrecord.config;

import com.google.common.base.CaseFormat;

public enum NamingStrategy {
    UPPER_CAMEL_TO_LOWER_UNDERSCORE(CaseFormat.UPPER_CAMEL, CaseFormat.LOWER_UNDERSCORE),
    LOWER_CAMEL_TO_LOWER_UNDERSCORE(CaseFormat.LOWER_CAMEL, CaseFormat.LOWER_UNDERSCORE);

    private final CaseFormat objectFormat;
    private final CaseFormat databaseFormat;

    NamingStrategy(CaseFormat objectFormat, CaseFormat databaseFormat) {
        this.objectFormat = objectFormat;
        this.databaseFormat = databaseFormat;
    }

    public String toDatabaseFormat(String objectFormatName) {
        return objectFormat.to(databaseFormat, objectFormatName);
    }

    public String toObjectFormat(String databaseFormatName) {
        return databaseFormat.to(objectFormat, databaseFormatName);
    }
}
