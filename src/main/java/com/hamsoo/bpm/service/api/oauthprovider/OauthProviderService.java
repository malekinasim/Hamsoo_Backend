package com.hamsoo.bpm.service.api.oauthprovider;

import com.hamsoo.bpm.model.dto.oauthprovider.OauthProviderDto;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.repository.jpa.oauthprovider.OauthProviderRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface OauthProviderService extends BaseService<OauthProvider, Long, OauthProviderRepository> {
    OauthProvider findByIdOrElseThrow(Long id);


    void deleteOauthProvider(Long id);


    List<OauthProviderDto> findAllProviderInfo();


    OauthProvider findByRegisterationId(String registrationId);


    Optional<OauthProvider> findByOrganIdAndType(Long organId, String type);
}