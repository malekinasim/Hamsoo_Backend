package com.hamsoo.bpm.service.impl.oauthProvider;


import com.hamsoo.bpm.model.dto.oauthprovider.OauthProviderDto;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.repository.jpa.oauthprovider.OauthProviderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hamsoo.bpm.common.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OauthProviderServiceImpl implements OauthProviderService {
    @Value(value = "${app.oauth2.authorizedRedirectUris}")
    private String redirectUri;

    @Value(value = "${hamsoo.server.address}")
    private String server;

    @Value(value = "${server.port}")
    private String port;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    private OauthProviderRepository oauthProviderRepository;

    public OauthProviderServiceImpl(OauthProviderRepository oauthProviderRepository) {
        this.oauthProviderRepository = oauthProviderRepository;
    }


    @Override
    public OauthProviderRepository getRepository() {
        return oauthProviderRepository;
    }


    @Override
    public OauthProvider findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.oauthProvider.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.oauthProvider.idIsNotValid"));
    }

    @Override
    public void deleteOauthProvider(Long id) {
        OauthProvider oauthProvider = findByIdOrElseThrow(id);
        delete(oauthProvider);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OauthProviderDto> findAllProviderInfo() {
        List<OauthProvider> oauthProviders = getRepository().findAll();

        String address = port.equals("80") ? server + contextPath : server + ":" + port + contextPath;
        return oauthProviders.stream().filter(oauthProvider -> oauthProvider.getOrgan() != null
                && !oauthProvider.getType().equals(OauthProvider.providerType.DAO.name()))
                .map(
                        oauthProvider -> new OauthProviderDto(oauthProvider, redirectUri, address)
                )
                .collect(Collectors.toList());

    }

    @Override
    public OauthProvider findByRegisterationId(String registrationId) {
        if (registrationId == null) {
            throw new BusinessException("error.oauthProvider.registrationIdIsNotValid");
        }
        return getRepository().findByRegisterationId(registrationId).orElseThrow(() ->
                new BusinessException("error.oauthProvider.registrationIdIsNotValid"));
    }

    @Override
    public Optional<OauthProvider> findByOrganIdAndType(Long organId, String type) {
        return getRepository().findByOrganIdAndType(organId,type);
    }


}