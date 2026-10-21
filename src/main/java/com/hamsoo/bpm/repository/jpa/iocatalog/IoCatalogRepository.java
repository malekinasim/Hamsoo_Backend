package com.hamsoo.bpm.repository.jpa.iocatalog;

import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IoCatalogRepository extends HamsooRepository<IoCatalog, Long> {

    Optional<IoCatalog> findByIdAndStatusAndOrganId(Long id, String status, Long organId);

}