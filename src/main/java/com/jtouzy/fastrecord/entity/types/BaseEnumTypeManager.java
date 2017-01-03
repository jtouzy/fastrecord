package com.jtouzy.fastrecord.entity.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.sql.Types;
import java.util.Map;

public abstract class BaseEnumTypeManager<T> implements TypeManager<T> {
    private BiMap<T,String> enumMapping;

    public BaseEnumTypeManager() {
        this.enumMapping = HashBiMap.create();
        this.initializeEnumMapping(enumMapping);
    }

    protected abstract void initializeEnumMapping(Map<T,String> enumMapping);

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public String convertToDatabase(T enumValue) {
        return enumMapping.get(enumValue);
    }

    @Override
    public T convertToObject(Object o) {
        return enumMapping.inverse().get(String.valueOf(o));
    }
}
