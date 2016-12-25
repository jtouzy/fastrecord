package com.jtouzy.fastrecord.entity.types;

import com.jtouzy.fastrecord.annotations.support.Converts;

import java.sql.Types;

@Converts(String.class)
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
        return objectFromDatabase.toString();
    }
}
