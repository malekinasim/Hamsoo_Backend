package com.hamsoo.bpm.repository.jpa.saleplanfeature;

import com.hamsoo.bpm.model.entity.saleplanfeature.SalePlanFeature;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SalePlanFeatureRepository extends HamsooRepository<SalePlanFeature,Long> {
}
