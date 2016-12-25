package com.jtouzy.fastrecord.repositories;

import java.util.Optional;

public interface MultipleIdRepository<T> {
    Optional<T> findById(Object... ids);
}
