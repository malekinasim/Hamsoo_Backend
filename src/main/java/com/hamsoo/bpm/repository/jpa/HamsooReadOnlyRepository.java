package com.hamsoo.bpm.repository.jpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class, readOnly = true)
@NoRepositoryBean
public interface HamsooReadOnlyRepository<T, ID> extends Repository<T, ID> {
    Optional<T> findById(ID id);

    List<T> findAll();
}