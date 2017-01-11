package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;
import com.jtouzy.fastrecord.utils.Priority;

import java.sql.Types;

@Converts(value = Integer.class, priority = Priority.NATIVE)
public class IntegerTypeManager implements TypeManager<Integer> {
    @Override
    public int getSqlType() {
        return Types.INTEGER;
    }

    @Override
    public String convertToDatabase(Integer objectFromType) {
        return objectFromType == null ? null : String.valueOf(objectFromType);
    }

    @Override
    public Integer convertToObject(Object objectFromDatabase) {
        return objectFromDatabase == null ? null : Integer.parseInt(String.valueOf(objectFromDatabase));
    }
}
