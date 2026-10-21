package com.hamsoo.bpm.repository.jpa.oauthprovider;


import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional(rollbackFor = Exception.class)
public interface OauthProviderRepository extends HamsooRepository<OauthProvider, Long> {
    Optional<OauthProvider> findByRegisterationId(String registrationId);


    @Query(

    )
    Optional<OauthProvider> findByOrganIdAndType(Long organId, String type);
}