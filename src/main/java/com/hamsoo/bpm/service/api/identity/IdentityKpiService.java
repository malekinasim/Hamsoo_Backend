package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityKpiDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityKpi;
import com.hamsoo.bpm.repository.jpa.identity.IdentityKpiRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public interface IdentityKpiService extends BaseService<IdentityKpi, Long, IdentityKpiRepository> {

    IdentityKpi findByIdOrElseThrow(Long id);

    IdentityKpi create(IdentityKpiDto identityKpiDto);

    void passive(Long id);

    IdentityKpiDto mapToDto(IdentityKpi identityKpi);

    SearchDto list(IdentityKpiDto identityKpiDto, int page, int size, String orderBy, Sort.Direction direction,
                   Function<IdentityKpi, IdentityKpiDto> mapper);

    Long countByKpiCatalogId(Long kpiCatalogId);

    IdentityKpi clone(IdentityKpi identityKpi);

    List<IdentityKpi> findActiveByModelVersionId(Long modelVersionId);

    void copyIdentityKpi(Long modelVersionIdFrom, Long modelVersionIdTo);

}
