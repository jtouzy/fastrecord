package com.jtouzy.fastrecord.repositories;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
}
