package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

public interface Processor {
    DbReadyStatementMetadata writeMetadata();
}
