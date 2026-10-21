package com.hamsoo.bpm.service.api.base;

import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID, R extends HamsooRepository<T, ID>> {

    R getRepository();

    default Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    default T getById(ID id) {
        return getRepository().getById(id);
    }

    default <S extends T> S save(S entity) {
        return getRepository().save(entity);
    }

    default <S extends T> List<S> saveAll(List<S> entityList) {
        return getRepository().saveAll(entityList);
    }

    default void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    default void delete(T entity) {
        getRepository().delete(entity);
    }

    default void deleteAll(Iterable<? extends T> entities) {
        getRepository().deleteAll(entities);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    default List<T> findAll(Specification<T> spec) {
        return getRepository().findAll(spec);
    }

    default Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    default Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return getRepository().findAll(spec, pageable);
    }

}

