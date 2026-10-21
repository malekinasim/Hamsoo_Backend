package com.hamsoo.bpm.service.api.organplan;

import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.repository.jpa.organplan.OrganPlanRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;


public interface OrganPlanService extends BaseService<OrganPlan,Long, OrganPlanRepository> {
    OrganPlan findByIdOrElseThrow(Long id);

    Optional<OrganPlan> getLastOrganSalePlan( Long organId);

    OrganPlanDto mapToDto(OrganPlan organPlan);

    OrganPlan create(OrganPlanDto organPlanDto);

    OrganPlan edit(OrganPlanDto organPlanDto);

    void passive(Long id);

    SearchDto list(OrganPlanDto organPlanDto, int page, int size, String orderBy, Sort.Direction direction);

    void CheckOrganUserCountLimitBaseOnLastOrganPlan(Long organId);

    void CheckOrganModelCountLimitBaseOnLastOrganPlan(Long organId);

    void CheckOrganCapacityLimitBaseOnLastOrganPlan(Long organId);

    OrganPlan expiredCurOrganPlan(Long organId);
}
