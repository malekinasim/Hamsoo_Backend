package com.hamsoo.bpm.service.impl.fcm;

import com.google.firebase.messaging.TopicManagementResponse;
import com.hamsoo.bpm.common.config.fcm.FcmClient;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.fcm.FcmTokenDto;
import com.hamsoo.bpm.model.entity.fcm.FcmToken;
import com.hamsoo.bpm.model.entity.fcm.FcmToken_;
import com.hamsoo.bpm.repository.jpa.fcm.FcmTokenRepository;
import com.hamsoo.bpm.service.api.fcm.FcmTokenService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FcmTokenServiceImpl implements FcmTokenService {

    public static final int MAX_USER_CLIENT_COUNT = 5;

    private final FcmTokenRepository fcmTokenRepository;
    private final UserService userService;
    private final FcmClient fcmClient;

    public FcmTokenServiceImpl(FcmTokenRepository fcmTokenRepository,
                               UserService userService,
                               FcmClient fcmClient) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userService = userService;
        this.fcmClient = fcmClient;
    }

    @Override
    public FcmTokenRepository getRepository() {
        return fcmTokenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FcmToken findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.fcmToken.idIsNotValid");
        }
        return getRepository().findById(id)
                .orElseThrow(() -> new BusinessException("error.fcmToken.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FcmToken create(FcmTokenDto fcmTokenDto) {
        Assert.notNull(fcmTokenDto, "fcmTokenDto is null");
        FcmToken fcmToken = new FcmToken();
        fcmToken.setUser(userService.findByIdOrElseThrow(SecurityUtils.getCurrentUserOrElseThrow().getId()));
        fcmToken.setToken(fcmTokenDto.getToken());
        fcmToken.setTimestamp(LocalDateTime.now());
        fcmToken.setOs(fcmTokenDto.getOs());
        fcmToken.setBrowser(fcmTokenDto.getBrowser());
        return save(fcmToken);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        FcmToken fcmToken = findByIdOrElseThrow(id);
        delete(fcmToken);
    }

    @Override
    @Transactional(readOnly = true)
    public FcmTokenDto mapToDto(FcmToken fcmToken) {
        Assert.notNull(fcmToken, "fcmToken is null");
        fcmToken = findByIdOrElseThrow(fcmToken.getId());
        FcmTokenDto fcmTokenDto = new FcmTokenDto();
        fcmTokenDto.setId(fcmToken.getId());
        fcmTokenDto.setToken(fcmToken.getToken());
        fcmTokenDto.setOs(fcmToken.getOs());
        fcmTokenDto.setBrowser(fcmToken.getBrowser());
        return fcmTokenDto;
    }

    /**
     * - check whether token already store in DB.
     * - if not stored in DB -> delete and unsubscribe oldest token(client)
     * - store and subscribe new token(client) .
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public FcmToken subscribe(FcmTokenDto fcmTokenDto) {
        Assert.notNull(fcmTokenDto, "fcmTokenDto is null");
        Optional<FcmToken> optionalFcmToken = getRepository()
                .findByTokenAndUserId(fcmTokenDto.getToken(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (optionalFcmToken.isPresent()) {
            if (optionalFcmToken.get().getTimestamp().isBefore(LocalDateTime.now().minusMonths(1))) {
                throw new BusinessException("TOKEN_MUST_TO_REFRESH");
            }
            throw new BusinessException("TOKEN_ALREADY_SUBSCRIBED");
        }
        Pageable pageable = PageUtils.getInstance(0, PageUtils.MAX_PAGE_SIZE, FcmToken_.TIMESTAMP, Sort.Direction.ASC);
        List<FcmToken> fcmTokenList = getRepository().findByUserId(SecurityUtils.getCurrentUserOrElseThrow().getId(), pageable);
        if (!CollectionUtils.isEmpty(fcmTokenList) && fcmTokenList.size() >= MAX_USER_CLIENT_COUNT) {
            List<FcmToken> firstNElementList = fcmTokenList.stream().limit(fcmTokenList.size() - MAX_USER_CLIENT_COUNT + 1).toList();
            deleteAndUnsubscribe(firstNElementList);
        }
        return createAndSubscribe(fcmTokenDto);
    }

    private void deleteAndUnsubscribe(List<FcmToken> fcmTokenList) {
        fcmTokenList.forEach(fcmToken -> {
            Optional<TopicManagementResponse> response = fcmClient.unsubscribe(fcmClient.getCurrentUserTopicName(),
                    Collections.singletonList(fcmToken.getToken()));
            if (response.isPresent() && response.get().getSuccessCount() == 1) {
                delete(fcmToken.getId());
            } else {
                throw new BusinessException("error.fcmToken.errorInCommunicationWithFcmServer");
            }
        });
    }

    private FcmToken createAndSubscribe(FcmTokenDto fcmTokenDto) {
        FcmToken fcmToken;
        Optional<TopicManagementResponse> response = fcmClient.subscribe(fcmClient.getCurrentUserTopicName(),
                Collections.singletonList(fcmTokenDto.getToken()));
        if (response.isPresent() && response.get().getSuccessCount() == 1) {
            fcmToken = create(fcmTokenDto);
        } else {
            throw new BusinessException("error.fcmToken.errorInCommunicationWithFcmServer");
        }
        return fcmToken;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void unsubscribe(FcmTokenDto fcmTokenDto) {
        Assert.notNull(fcmTokenDto, "fcmTokenDto is null");
        Optional<FcmToken> optionalFcmToken = getRepository()
                .findByTokenAndUserId(fcmTokenDto.getToken(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (optionalFcmToken.isEmpty()) {
            throw new BusinessException("error.fcmToken.tokenDontRegisterForUser");
        }
        deleteAndUnsubscribe(Collections.singletonList(optionalFcmToken.get()));
    }

}
