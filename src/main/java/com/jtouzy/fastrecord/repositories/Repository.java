package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.StatementException;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    T create(T object) throws StatementException;
    T update(T object) throws StatementException;
}
