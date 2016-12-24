package com.jtouzy.fastrecord.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<T,ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
}
