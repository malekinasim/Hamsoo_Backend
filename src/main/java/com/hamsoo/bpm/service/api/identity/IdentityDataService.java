package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityDataDto;

public interface IdentityDataService {

    IdentityDataDto updateIdentityDataSchema(Long modelVersionId, IdentityDataDto identityDataDto);

    IdentityDataDto getIdentityDataSchema(Long modelVersionId);

}
