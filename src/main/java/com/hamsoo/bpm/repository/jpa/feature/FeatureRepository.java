package com.hamsoo.bpm.repository.jpa.feature;

import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FeatureRepository extends HamsooRepository<Feature, Long> {

    @Query(value = "select f from Feature f where f.id= :id and f.status= :status")
    Optional<Feature> findByIdAndStatus(Long id,String status);
}
