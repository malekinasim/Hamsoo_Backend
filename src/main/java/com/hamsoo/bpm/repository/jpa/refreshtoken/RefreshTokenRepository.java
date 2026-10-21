package com.hamsoo.bpm.repository.jpa.refreshtoken;

import com.hamsoo.bpm.model.entity.refreshtoken.RefreshToken;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface RefreshTokenRepository extends HamsooRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    int deleteAllByAccount_IdAndExpiryDateBefore(Long accountId, LocalDateTime localDateTime);
    int deleteByToken(String token);
}
