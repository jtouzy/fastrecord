package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;
import com.jtouzy.fastrecord.utils.Priority;

import java.sql.Types;

@Converts(value = String.class, priority = Priority.NATIVE)
public class StringTypeManager implements TypeManager<String> {
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public String convertToDatabase(String objectFromType) {
        return objectFromType;
    }

    @Override
    public String convertToObject(Object objectFromDatabase) {
        return objectFromDatabase == null ? null : objectFromDatabase.toString();
    }
}
