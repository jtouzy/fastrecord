package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;
import com.jtouzy.fastrecord.utils.Priority;

import java.sql.Types;

@Converts(value = Boolean.class, priority = Priority.NATIVE)
public class BooleanTypeManager implements TypeManager<Boolean> {
    @Override
    public int getSqlType() {
        return Types.BOOLEAN;
    }

    @Override
    public String convertToDatabase(Boolean objectFromType) {
        return objectFromType == null ? null : String.valueOf(objectFromType);
    }

    @Override
    public Boolean convertToObject(Object objectFromDatabase) {
        return objectFromDatabase == null ? null : Boolean.parseBoolean(String.valueOf(objectFromDatabase));
    }
}
