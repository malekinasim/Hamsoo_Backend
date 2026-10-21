package com.hamsoo.bpm.service.impl.notification;

import com.hamsoo.bpm.common.config.fcm.FcmClient;
import com.hamsoo.bpm.common.config.fcm.FcmRequest;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.ValidationException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.StringUtil;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.notificationapiparam.NotificationApiParamDto;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.notification.NotificationRepository;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.notificationapiaddress.NotificationApiAddressService;
import com.hamsoo.bpm.service.api.notificationapiparam.NotificationApiParamService;
import com.hamsoo.bpm.service.api.notificationaudience.NotificationAudienceService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.validator.notification.NotificationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationApiAddressService notificationApiAddressService;
    private final NotificationApiParamService notificationApiParamService;
    private final NotificationAudienceService notificationAudienceService;
    private final NotificationValidator notificationValidator;
    private final UserService userService;
    private final OrganService organService;
    private final FcmClient fcmClient;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public static final String NOTIFICATION_END_POINT = "/topic/notification/{0}";
    public static final String NOTIFICATION_END_POINT_PREFIX = "/topic/notification/";

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationApiAddressService notificationApiAddressService,
                                   NotificationApiParamService notificationApiParamService,
                                    NotificationAudienceService notificationAudienceService,
                                   NotificationValidator notificationValidator,
                                   UserService userService,
                                   OrganService organService,
                                   FcmClient fcmClient,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.notificationApiAddressService = notificationApiAddressService;
        this.notificationApiParamService = notificationApiParamService;
        this.notificationAudienceService = notificationAudienceService;
        this.notificationValidator = notificationValidator;
        this.userService = userService;
        this.organService = organService;
        this.fcmClient = fcmClient;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public NotificationRepository getRepository() {
        return notificationRepository;
    }

    @Override
    public Notification findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.notification.idIsNotValid");
        }
        return getRepository().findNotificationById(id, SecurityUtils.getCurrentUserOrElseThrow().getId())
                .orElseThrow(() -> new BusinessException("error.notification.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Notification create(NotificationDto notificationDto) {
        Assert.notNull(notificationDto, "notificationDto is null");
        Notification notification = new Notification();
        if (StringUtils.hasText(notificationDto.getNotificationApiAddressCode())) {
            NotificationApiAddress notificationApiAddress = notificationApiAddressService.findByCodeOrElseThrow(notificationDto.getNotificationApiAddressCode());
            notification.setNotificationApiAddress(notificationApiAddress);

             notificationDto.setNotificationApiParamDtoList(StringUtil.getParameters(notificationApiAddress.getUri()).stream().map(p ->{
                    NotificationApiParamDto  notificationApiParamDto = new NotificationApiParamDto();
                    notificationApiParamDto.setKey(p);
                    notificationApiParamDto.setValue(notificationDto.getNotificationParamValues().get(p).toString());
                    return notificationApiParamDto;
            }).collect(Collectors.toList()));
        }

        notification.setEventType(notificationDto.getEventType());
        notification.setNotification(notificationDto.getNotification());
        notification.setEventDate(LocalDateTime.now());
        notification.setFromDate(notificationDto.getFromDate());
        notification.setToDate(notificationDto.getToDate());
        notification.setStatus(Status.ACTIVE.name());
        notification.setEntityType(notificationDto.getEntityType());
        notification.setRefId(notificationDto.getRefId());
        notification.setOrgan(organService.getById(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        List<NotificationApiParam> notificationApiParamList = notificationApiParamService.mapToEntity(notificationDto.getNotificationApiParamDtoList(), notification);
        notification.setNotificationApiParamList(notificationApiParamList);
        List<NotificationAudience> notificationAudienceList = notificationAudienceService.mapToEntity(notificationDto.getNotificationAudienceDtoList(), notification);
        notification.setNotificationAudienceList(notificationAudienceList);
        return save(notification);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Notification validateAndCreate(NotificationDto notificationDto) {
        BindException bindException = new BindException(notificationDto, "notificationDto");
        notificationValidator.validate(notificationDto, bindException);
        if (bindException.hasErrors()) {
            throw new ValidationException(bindException);
        }
        Notification notification= create(notificationDto);

        notification.getNotificationAudienceList().forEach(audience -> {
            //WebpushNotification
            FcmRequest fcmRequest = new FcmRequest.Builder()
                    .topicName(fcmClient.getTopicName(audience.getUser().getId())).notificationBody(notification.getNotification()).build();
            fcmClient.send(fcmRequest);
            //websocketNotification
            sendNotification(audience.getUser().getId());
        });
        return notification;
    }
    private void sendNotification(Long UserId) {
        Long unreadNotificationCount = notificationAudienceService.getUnreadNotificationByUserId(UserId,SecurityUtils.getCurrentOrgan().get().getId());//because of Transcaction, new notification count not in this method result
        simpMessagingTemplate.convertAndSend(MessageFormat.format(NOTIFICATION_END_POINT, UserId.toString()), unreadNotificationCount);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto mapToDto(Notification notification) {
        Assert.notNull(notification, "notification is null");
        Assert.notNull(notification.getId(), "notification's id is null");
        notification = getById(notification.getId());
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(notification.getId());
        if (notification.getNotificationApiAddress() != null) {
            String uri = generateUri(notification.getNotificationApiAddress().getUri(), notification.getNotificationApiParamList());
            notificationDto.setUri(uri);
        }
        notificationDto.setEventType(notification.getEventType());
        notificationDto.setNotification(notification.getNotification());
        notificationDto.setEventDate(notification.getEventDate());
        notificationDto.setFromDate(notification.getFromDate());
        notificationDto.setToDate(notification.getToDate());
        notificationDto.setEntityType(notification.getEntityType());
        notificationDto.setRefId(notification.getRefId());
        notificationDto.setCreatedBy(userService.mapToDto(notification.getCreatedBy()));
        NotificationAudienceDto notificationAudienceDto = new NotificationAudienceDto();

        Optional<NotificationAudience> notificationAudience= notification.getNotificationAudienceList().stream().filter(s ->
                s.getUser().getId().equals(SecurityUtils.getCurrentUserOrElseThrow().getId())
        ).findFirst();
        if(notificationAudience.isPresent()) {
            notificationAudienceDto.setId(notificationAudience.get().getId());
            notificationAudienceDto.setReadStatus(notificationAudience.get().isReadStatus());
        }
        List<NotificationAudienceDto> notificationAudienceDtoList = new ArrayList<>();
        notificationAudienceDtoList.add(notificationAudienceDto);
        notificationDto.setNotificationAudienceDtoList(notificationAudienceDtoList);

        return notificationDto;
    }

    private String generateUri(String notificationApiAddressUri, List<NotificationApiParam> notificationApiParamList) {
        if (CollectionUtils.isEmpty(notificationApiParamList)) {
            return notificationApiAddressUri;
        }
        Map<String, String> param = notificationApiParamList.stream()
                .collect(Collectors.toMap(NotificationApiParam::getKey, NotificationApiParam::getValue));
        return StringUtil.applyParameter(notificationApiAddressUri, param);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        Notification notification = findByIdOrElseThrow(id);
        notification.setStatus(Status.PASSIVE.name());
        save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto notificationOfCurrentUser(int page, int size, String orderBy, Sort.Direction direction, Boolean readStatus) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<Notification> notificationPage = getRepository().findByUserId(SecurityUtils.getCurrentUserOrElseThrow().getId(), SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable, readStatus);
        List<NotificationDto> content = notificationPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto notificationByModelVersion(Long modelVersionId, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<Notification> notificationPage = getRepository().findByRefIdAndEntityType(modelVersionId, Notification.EntityType.MODEL_VERSION.getValue(), pageable);
        List<NotificationDto> content = notificationPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId, String entityType) {
        Assert.notNull(newModelVersionId, "newModelVersionId is null");
        Assert.notNull(oldModelVersionId, "oldModelVersionId is null");
        Assert.notNull(entityType, "entityType is null");
        getRepository().updateModelVersionId(newModelVersionId, oldModelVersionId, entityType);
    }

}
