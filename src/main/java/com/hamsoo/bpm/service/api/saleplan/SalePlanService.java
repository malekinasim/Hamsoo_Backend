package com.hamsoo.bpm.service.api.saleplan;

import com.hamsoo.bpm.model.dto.saleplan.SalePlanDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.repository.jpa.saleplan.SalePlanRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface SalePlanService extends BaseService<SalePlan, java.lang.Long, SalePlanRepository> {
    SalePlan findByIdOrElseThrow(java.lang.Long id);

    Optional<SalePlan> findBySaleServiceTypeAndStatus(String serviceType,String status);

    SalePlanDto mapToDto(SalePlan salePlan);

    SalePlan create(SalePlanDto salePlanDto);

    SalePlan edit(SalePlanDto salePlanDto);

    void passive(java.lang.Long id);

    SearchDto list(SalePlanDto salePlanDto, int page, int size, String orderBy, Sort.Direction direction);
}
