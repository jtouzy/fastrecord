package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converts(LocalDateTime.class)
public class LocalDateTimeTypeManager implements TypeManager<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public int getSqlType() {
        return Types.TIMESTAMP;
    }

    @Override
    public String convertToDatabase(LocalDateTime objectFromType) {
        return formatter.format(objectFromType);
    }

    @Override
    public LocalDateTime convertToObject(Object objectFromDatabase) {
        return LocalDateTime.parse(String.valueOf(objectFromDatabase), formatter);
    }
}
