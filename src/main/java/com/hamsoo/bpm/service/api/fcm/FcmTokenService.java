package com.hamsoo.bpm.service.api.fcm;

import com.hamsoo.bpm.model.dto.fcm.FcmTokenDto;
import com.hamsoo.bpm.model.entity.fcm.FcmToken;
import com.hamsoo.bpm.service.api.base.BaseService;
import com.hamsoo.bpm.repository.jpa.fcm.FcmTokenRepository;

public interface FcmTokenService extends BaseService<FcmToken, Long, FcmTokenRepository> {

    FcmToken findByIdOrElseThrow(Long id);

    FcmToken create(FcmTokenDto fcmTokenDto);

    void delete(Long id);

    FcmTokenDto mapToDto(FcmToken fcmToken);

    FcmToken subscribe(FcmTokenDto fcmTokenDto);

    void unsubscribe(FcmTokenDto fcmTokenDto);
}
