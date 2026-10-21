package com.hamsoo.bpm.repository.jpa.resourcetype;

import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ResourceTypeRepository extends HamsooRepository<ResourceType, Long> {

    Optional<ResourceType> findByIdAndStatus(Long id, String status);

    List<ResourceType> findByStatus(String status, Pageable pageable);


}