package com.jtouzy.fastrecord.repositories;

import com.jtouzy.fastrecord.builders.StatementException;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    T create(T object) throws StatementException;
    List<T> create(List<T> objects) throws StatementException;
    T update(T object) throws StatementException;
    void delete(T object) throws StatementException;
}
