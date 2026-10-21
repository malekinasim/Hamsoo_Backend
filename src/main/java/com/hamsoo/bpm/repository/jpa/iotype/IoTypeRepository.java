package com.hamsoo.bpm.repository.jpa.iotype;

import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IoTypeRepository extends HamsooRepository<IoType, Long> {

    List<IoType> findByStatus(String status, Pageable pageable);

    Optional<IoType> findByIdAndStatus(Long id, String status);

}