package com.jtouzy.fastrecord.statements.processing;

import java.util.ArrayList;
import java.util.List;

public class BaseDbReadyStatementMetadata implements DbReadyStatementMetadata {
    private final StringBuilder sqlString;
    private final List<DbReadyStatementParameter> parameters;

    public BaseDbReadyStatementMetadata(String sqlString) {
        this(sqlString, null);
    }

    public BaseDbReadyStatementMetadata(String sqlString, List<DbReadyStatementParameter> parameters) {
        this.sqlString = new StringBuilder();
        this.sqlString.append(sqlString);
        this.parameters = parameters == null ? new ArrayList<>() : parameters;
    }

    @Override
    public StringBuilder getSqlString() {
        return this.sqlString;
    }

    @Override
    public void merge(DbReadyStatementMetadata metadata) {
        this.sqlString.append(metadata.getSqlString());
        this.parameters.addAll(metadata.getParameters());
    }

    @Override
    public void addParameter(DbReadyStatementParameter parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public List<DbReadyStatementParameter> getParameters() {
        return parameters;
    }
}
