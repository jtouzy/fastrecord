package com.jtouzy.fastrecord.config;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("FastRecord.Core.Configuration")
public class FastRecordConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FastRecordConfiguration.class);
    // Global prefix
    private static final String FASTRECORD_PROPERTIES_PREFIX = "fastrecord.";
    private static final String MAPPING_PROPERTIES_PREFIX = FASTRECORD_PROPERTIES_PREFIX + "mapping.";
    private static final String OPTIONS_PROPERTIES_PREFIX = FASTRECORD_PROPERTIES_PREFIX + "options.";
    private static final String SYNTAX_PROPERTIES_PREFIX = FASTRECORD_PROPERTIES_PREFIX + "syntax.";
    // All options with prefix
    private static final String TABLE_NAMING_STRATEGY = MAPPING_PROPERTIES_PREFIX + "table_naming_strategy";
    private static final String COLUMN_NAMING_STRATEGY = MAPPING_PROPERTIES_PREFIX + "column_naming_strategy";
    private static final String PRINT_SQL = OPTIONS_PROPERTIES_PREFIX + "print_sql";
    private static final String COLUMN_ALIAS_SEPARATOR = SYNTAX_PROPERTIES_PREFIX + "column_alias_separator";

    private final Environment environment;
    private NamingStrategy tableNamingStrategy;
    private NamingStrategy columnNamingStrategy;
    private String columnAliasSeparator;
    private boolean printSql;

    @Autowired
    public FastRecordConfiguration(Environment environment) {
        this.environment = environment;
        initializeProperties();
    }

    private void initializeProperties() {
        // Print SQL
        String property = environment.getProperty(PRINT_SQL);
        setPrintSql(property != null && Boolean.parseBoolean(property));
        // Table Naming Strategy
        setTableNamingStrategy(
                loadNamingStrategy(TABLE_NAMING_STRATEGY, NamingStrategy.UPPER_CAMEL_TO_LOWER_UNDERSCORE));
        // Column Naming Strategy
        setColumnNamingStrategy(
                loadNamingStrategy(COLUMN_NAMING_STRATEGY, NamingStrategy.LOWER_CAMEL_TO_LOWER_UNDERSCORE));
        // Column Alias Separator
        property = environment.getProperty(COLUMN_ALIAS_SEPARATOR);
        setColumnAliasSeparator(property == null ? "$$" : property);
    }

    private NamingStrategy loadNamingStrategy(String propertyName, NamingStrategy defaultStrategy) {
        NamingStrategy namingStrategy = null;
        String property = environment.getProperty(propertyName);
        if (!Strings.isNullOrEmpty(property)) {
            try {
                namingStrategy = NamingStrategy.valueOf(property);
            } catch (IllegalArgumentException ex) {
                logger.error("Unrecognized naming strategy [{}], use default", property);
            }
        }
        return namingStrategy == null ? defaultStrategy : namingStrategy;
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

    public boolean isPrintSql() {
        return printSql;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }

    public String getColumnAliasSeparator() {
        return columnAliasSeparator;
    }

    public void setColumnAliasSeparator(String columnAliasSeparator) {
        this.columnAliasSeparator = columnAliasSeparator;
    }
}
