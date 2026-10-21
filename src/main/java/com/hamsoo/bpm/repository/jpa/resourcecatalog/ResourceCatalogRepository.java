package com.hamsoo.bpm.repository.jpa.resourcecatalog;

import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ResourceCatalogRepository extends HamsooRepository<ResourceCatalog, Long> {

    Optional<ResourceCatalog> findByIdAndStatusAndOrganId(Long id, String status, Long organId);

    @Query(" select rc from ResourceCatalog rc where rc.resourceType.id=:id and rc.resourceType.status='ACTIVE' and rc.status='ACTIVE' ")
    List<ResourceCatalog> getlistByResourceTypeId(Long id);

}