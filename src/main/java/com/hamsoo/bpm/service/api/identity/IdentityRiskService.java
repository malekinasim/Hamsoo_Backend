package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityRiskDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityRisk;
import com.hamsoo.bpm.repository.jpa.identity.IdentityRiskRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public interface IdentityRiskService  extends BaseService<IdentityRisk, Long, IdentityRiskRepository> {

    IdentityRisk findByIdOrElseThrow(Long id);

    IdentityRisk create(IdentityRiskDto IdentityRiskDto);

    void passive(Long id);

    IdentityRiskDto mapToDto(IdentityRisk identityRisk);

    SearchDto list(IdentityRiskDto identityRiskDto, int page, int size, String orderBy, Sort.Direction direction,
                   Function<IdentityRisk, IdentityRiskDto> mapper);

    IdentityRisk clone(IdentityRisk identityRisk);

    List<IdentityRisk> findActiveByModelVersionId(Long modelVersionId);

    void copyIdentityRisk(Long modelVersionIdFrom, Long modelVersionIdTo);

    IdentityRisk edit(Long id, IdentityRiskDto identityRiskDto);

}
