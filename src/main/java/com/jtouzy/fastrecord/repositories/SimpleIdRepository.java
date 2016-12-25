package com.jtouzy.fastrecord.repositories;

import java.util.Optional;

public interface SimpleIdRepository<T,ID> {
    Optional<T> findById(ID id);
}
