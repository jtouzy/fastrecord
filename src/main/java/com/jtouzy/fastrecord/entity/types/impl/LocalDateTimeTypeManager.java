package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converts(LocalDateTime.class)
public class LocalDateTimeTypeManager implements TypeManager<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    @Override
    public int getSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public String convertToDatabase(LocalDateTime objectFromType) {
        return objectFromType == null ? null : formatter.format(objectFromType);
    }

    @Override
    public LocalDateTime convertToObject(Object objectFromDatabase) {
        return objectFromDatabase == null ? null : LocalDateTime.parse(String.valueOf(objectFromDatabase), formatter);
    }
}
