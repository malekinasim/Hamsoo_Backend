package com.hamsoo.bpm.controller.modelversion;

import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.model.dto.modelversion.TeamWorkDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.Optional;

@Controller
public class TeamWorkController {

    // parameter 0 is a modelVersionId
    public static final String TEAM_WORK_END_POINT = "/topic/model-workspace/{0}";
    public static final String TEAM_WORK_END_POINT_PREFIX = "/topic/model-workspace/";


    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMemberService modelMemberService;
    private final UserService userService;

    public TeamWorkController(SimpMessagingTemplate simpMessagingTemplate,
                              ModelMemberService modelMemberService,
                              UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.modelMemberService = modelMemberService;
        this.userService = userService;
    }

    @MessageMapping("/left-model/{modelVersionId}")
    public void leftModel(@DestinationVariable String modelVersionId, Principal principal) {
        Optional<UserDto> optionalUser = userService.getUser(principal);
        UserDto user = optionalUser.orElse(new UserDto());
        String leftMessage = ResourceBundleUtils.getMessage("title.teamwork.leftModel", user.getFullName());
        TeamWorkDto teamWorkDto = new TeamWorkDto(leftMessage, user.getFullName(), principal.getName(), TeamWorkDto.MessageType.LEAVE, modelVersionId);
//        modelMemberService.changeMemberMode(principal.getName(), Long.valueOf(modelVersionId), ModelMember.ModeType.OFFLINE);
        simpMessagingTemplate.convertAndSend(MessageFormat.format(TEAM_WORK_END_POINT, modelVersionId.toString()), teamWorkDto);

    }

}
