package com.hamsoo.bpm.repository.jpa.fcm;

import com.hamsoo.bpm.model.entity.fcm.FcmToken;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface FcmTokenRepository extends HamsooRepository<FcmToken, Long> {

    Optional<FcmToken> findByTokenAndUserId(String token, Long userId);

    List<FcmToken> findByUserId(Long userId, Pageable pageable);

}
