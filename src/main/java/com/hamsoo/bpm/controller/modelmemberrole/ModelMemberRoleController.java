package com.hamsoo.bpm.controller.modelmemberrole;


import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberUserDtoImpl;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.modelversion.TeamWorkDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.validator.modelmemberrole.ModelMemberRoleValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;

import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT;

@RestController
@RequestMapping("/api/v1")
public class ModelMemberRoleController {

    private final ModelMemberRoleService modelMemberRoleService;
    private final ModelMemberRoleValidator modelMemberRoleValidator;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ModelMemberRoleController(ModelMemberRoleService modelMemberRoleService,
                                     ModelMemberRoleValidator modelMemberRoleValidator, SimpMessagingTemplate simpMessagingTemplate) {
        this.modelMemberRoleService = modelMemberRoleService;
        this.modelMemberRoleValidator = modelMemberRoleValidator;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @InitBinder("modelMemberRoleDto")
    public void initModelMemberRoleSchemaBinder(WebDataBinder binder) {
            binder.setValidator(modelMemberRoleValidator);

    }

    @GetMapping("/model-member-role/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelMemberRoleId(#id,@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> getModelMemberById(@PathVariable("id") Long id) {
        ModelMemberRole modelMemberRole = modelMemberRoleService.findByIdOrElseThrow(id);
        ModelMemberRoleDto modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
        return new ResponseEntity<>(modelMemberRoleDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-member-role")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelMemberRoleDto.modelVersionId,@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> saveModelMember(@Valid @RequestBody ModelMemberRoleDto modelMemberRoleDto) {
        ModelMemberRole modelMemberRole = modelMemberRoleService.create(modelMemberRoleDto);
        modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
        sendModeMemberChangeNotification(modelMemberRoleDto.getModelVersionId());
        return new ResponseEntity<>(modelMemberRoleDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/model-member-role")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelMemberRoleDto.modelVersionId,@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> editModelMember(@Valid @RequestBody ModelMemberRoleDto modelMemberRoleDto) {
        ModelMemberRole modelMemberRole = modelMemberRoleService.edit(modelMemberRoleDto);
        modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
        sendModeMemberChangeNotification(modelMemberRoleDto.getModelVersionId());
        return new ResponseEntity<>(modelMemberRoleDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-member-role/changeOwner")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelMemberRoleDto.modelVersionId,@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> changeOwner(@Valid @RequestBody ModelMemberRoleDto modelMemberRoleDto) {
        ModelMemberRole modelMemberRole = modelMemberRoleService.changeOwner(modelMemberRoleDto);
        modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
        sendModeMemberChangeNotification(modelMemberRoleDto.getModelVersionId());
        return new ResponseEntity<>(modelMemberRoleDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/model-member-role/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelMemberRoleId(#id,@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> passiveModelMember(@PathVariable Long id) {
       ModelMemberRole modelMemberRole= modelMemberRoleService.passive(id);
        sendModeMemberChangeNotification(modelMemberRole.getModelMember().getModelVersion().getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private void sendModeMemberChangeNotification(Long modelVersionId) {
        String fullName = SecurityUtils.getCurrentUserOrElseThrow().getFullName();
        String username = SecurityUtils.getCurrentAccountOrElseThrow().getUsername();
        String changeMessage = ResourceBundleUtils.getMessage("title.teamwork.updateModelMember");
        TeamWorkDto teamWorkDto = new TeamWorkDto(changeMessage, fullName, username, TeamWorkDto.MessageType.UPDATE_MODEL_MEMBER, modelVersionId.toString());
        simpMessagingTemplate.convertAndSend(MessageFormat.format(TEAM_WORK_END_POINT, modelVersionId.toString()), teamWorkDto);
    }
    @PostMapping(value = "/model-member-role/list")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelMemberRoleDto.modelVersionId,@resourceItemService.MODEL_MEMBERS_LIST)")
    public ResponseEntity<?> getModelMemberList(@RequestBody ModelMemberRoleDto modelMemberRoleDto,
                                                @RequestParam int page, @RequestParam int size,
                                                @RequestParam(required = false) String orderBy,
                                                @RequestParam(required = false) Sort.Direction direction) {

        SearchDto searchDto = modelMemberRoleService.list(modelMemberRoleDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping(value = "/model-member-role/list/user/{modelVersionId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_MEMBERS_LIST)")
    public ResponseEntity<?> getModelMemberUserList(@PathVariable Long modelVersionId) {

        List<ModelMemberUserDtoImpl> userDtoList = modelMemberRoleService.userList(modelVersionId);
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/model-member-role/curuser/list/{modelVersionId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_MEMBERS_LIST)")
    public ResponseEntity<?> curUserlist(@PathVariable Long modelVersionId) {
        List<ModelMemberRoleDto> modelMemberRoleDtos = modelMemberRoleService.curUserModelRoleList(modelVersionId);
        return new ResponseEntity<>(modelMemberRoleDtos, HttpStatus.OK);
    }


}
