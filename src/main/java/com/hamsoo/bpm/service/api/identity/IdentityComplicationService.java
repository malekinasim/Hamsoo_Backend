package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityComplicationDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityComplication;
import com.hamsoo.bpm.repository.jpa.identity.IdentityComplicationRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public interface IdentityComplicationService extends BaseService<IdentityComplication, Long, IdentityComplicationRepository> {

    IdentityComplication findByIdOrElseThrow(Long id);

    IdentityComplication create(IdentityComplicationDto identityComplicationDto);

    void passive(Long id);

    IdentityComplicationDto mapToDto(IdentityComplication identityComplication);

    SearchDto list(IdentityComplicationDto identityComplicationDto, int page, int size, String orderBy, Sort.Direction direction, Function<IdentityComplication, IdentityComplicationDto> mapper);

//    Long countByKpiCatalogId(Long identityComplicationId);

    IdentityComplication clone(IdentityComplication identityComplication);

    List<IdentityComplication> findActiveByModelVersionId(Long modelVersionId);

    void copyIdentityComplication(Long modelVersionIdFrom, Long modelVersionIdTo);

    IdentityComplication edit(Long id, IdentityComplicationDto identityComplicationDto);

}
