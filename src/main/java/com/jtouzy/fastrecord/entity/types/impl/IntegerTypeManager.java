package com.jtouzy.fastrecord.entity.types.impl;

import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.entity.types.TypeManager;

import java.sql.Types;

@Converts(Integer.class)
public class IntegerTypeManager implements TypeManager<Integer> {
    @Override
    public int getSqlType() {
        return Types.INTEGER;
    }

    @Override
    public String convertToDatabase(Integer objectFromType) {
        return String.valueOf(objectFromType);
    }

    @Override
    public Integer convertToObject(Object objectFromDatabase) {
        return Integer.parseInt(String.valueOf(objectFromDatabase));
    }
}
