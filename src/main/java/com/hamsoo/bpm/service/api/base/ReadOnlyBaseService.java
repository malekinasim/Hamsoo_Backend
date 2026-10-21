package com.hamsoo.bpm.service.api.base;

import com.hamsoo.bpm.repository.jpa.HamsooReadOnlyRepository;

import java.util.List;
import java.util.Optional;

public interface ReadOnlyBaseService<T, ID, R extends HamsooReadOnlyRepository<T, ID>> {
    R getRepository();

    default Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }
}
