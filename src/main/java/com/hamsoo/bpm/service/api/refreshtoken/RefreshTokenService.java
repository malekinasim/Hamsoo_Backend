package com.hamsoo.bpm.service.api.refreshtoken;

import com.hamsoo.bpm.model.entity.refreshtoken.RefreshToken;
import com.hamsoo.bpm.repository.jpa.refreshtoken.RefreshTokenRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.Optional;

public interface RefreshTokenService extends BaseService<RefreshToken, Long, RefreshTokenRepository> {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long accountId);

    RefreshToken verifyExpiration(RefreshToken token);


    int deleteAllExpiredByAccountId(Long id);

    int deleteByToken(String Token);
}
