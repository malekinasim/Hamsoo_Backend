package com.hamsoo.bpm.service.api.saleplanfeature;

import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.saleplanfeature.SalePlanFeature;
import com.hamsoo.bpm.repository.jpa.saleplanfeature.SalePlanFeatureRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SalePlanFeatureService extends BaseService<SalePlanFeature,Long, SalePlanFeatureRepository> {
    SalePlanFeature findByIdOrElseThrow(Long id);

    SalePlanFeature create(SalePlanFeatureDto salePlanFeatureDto);

    SalePlanFeatureDto mapToDto(SalePlanFeature salePlanFeature);

    void delete(Long id);

    SearchDto list(SalePlanFeatureDto salePlanFeatureDto, int page, int size, String orderBy, Sort.Direction direction);

    SalePlanFeature edit(SalePlanFeatureDto salePlanFeatureDto);
    List<SalePlanFeatureDto> findAllFeatureOfSalePlan(Long salePlanId);

//    List<SalePlanFeatureDto> findAllFeatureOfSalePlanByFeatureCalcTypeList(Long id);
//
//    List<SalePlanFeatureDto> findAllFeatureOfSalePlanByFeatureCalcTypeList(Long salePlanId, List<String> featureCalcTypeList);
    
}
