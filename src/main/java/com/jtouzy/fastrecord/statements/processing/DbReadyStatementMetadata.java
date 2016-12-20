package com.jtouzy.fastrecord.statements.processing;

import java.util.List;

public interface DbReadyStatementMetadata {
    StringBuilder getSqlString();
    void merge(DbReadyStatementMetadata metadata);
    void addParameter(DbReadyStatementParameter parameter);
    List<DbReadyStatementParameter> getParameters();
    void clear();
}
