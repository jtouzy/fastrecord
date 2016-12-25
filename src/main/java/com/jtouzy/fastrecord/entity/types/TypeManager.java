package com.jtouzy.fastrecord.entity.types;

public interface TypeManager<T> {
    int getSqlType();
    String convertToDatabase(T objectFromType);
    T convertToObject(Object objectFromDatabase);
}
