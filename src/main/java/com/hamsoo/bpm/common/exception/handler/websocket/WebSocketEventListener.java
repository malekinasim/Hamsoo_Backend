package com.hamsoo.bpm.common.exception.handler.websocket;

import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.model.dto.modelversion.TeamWorkDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.notificationaudience.NotificationAudienceService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.text.MessageFormat;
import java.util.Optional;

import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT;
import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT_PREFIX;
import static com.hamsoo.bpm.service.impl.notification.NotificationServiceImpl.NOTIFICATION_END_POINT;
import static com.hamsoo.bpm.service.impl.notification.NotificationServiceImpl.NOTIFICATION_END_POINT_PREFIX;

@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMemberService modelMemberService;
    private final UserService userService;

    private final NotificationAudienceService notificationAudienceService;
    public WebSocketEventListener(SimpMessagingTemplate simpMessagingTemplate,
                                  ModelMemberService modelMemberService,
                                  UserService userService, NotificationAudienceService notificationAudienceService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.modelMemberService = modelMemberService;
        this.userService = userService;
        this.notificationAudienceService = notificationAudienceService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        LOGGER.info(String.format("web socket connect with session id : %s", event.getMessage().getHeaders().get("simpSessionId")));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        LOGGER.info(String.format("web socket disconnect with session id : %s", event.getSessionId()));
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();
        String simpDestination = (String) message.getHeaders().get("simpDestination");

        if (StringUtils.hasText(simpDestination) &&
                simpDestination.startsWith(TEAM_WORK_END_POINT_PREFIX) &&
                event.getUser() != null) {
            Optional<UserDto> optionalUser = userService.getUser(event.getUser());
            UserDto user = optionalUser.orElse(new UserDto());
            String joinMessage = ResourceBundleUtils.getMessage("title.teamwork.joinModel", user.getFullName());
            String modelVersionId = simpDestination.substring(TEAM_WORK_END_POINT_PREFIX.length());
            TeamWorkDto teamWorkDto = new TeamWorkDto(joinMessage, user.getFullName(), event.getUser().getName(), TeamWorkDto.MessageType.JOIN, modelVersionId);
//            modelMemberService.changeMemberMode(event.getUser().getName(), Long.valueOf(modelVersionId), ModelMember.ModeType.ONLINE);
            simpMessagingTemplate.convertAndSend(MessageFormat.format(TEAM_WORK_END_POINT, modelVersionId.toString()), teamWorkDto);
        }
        if (StringUtils.hasText(simpDestination) &&
                simpDestination.startsWith(NOTIFICATION_END_POINT_PREFIX) &&
                event.getUser() != null) {
            Optional<UserDto> optionalUser = userService.getUser(event.getUser());
            UserDto user = optionalUser.orElse(new UserDto());
            Long unreadNotificationCount = notificationAudienceService.getUnreadNotificationByUserId(user.getId(),user.getCurOrgan().getId());
            simpMessagingTemplate.convertAndSend(MessageFormat.format(NOTIFICATION_END_POINT, user.getId().toString()), unreadNotificationCount);

        }
    }


    @EventListener
    public void handleSessionSubscribeEvent(SessionUnsubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();

    }

}
