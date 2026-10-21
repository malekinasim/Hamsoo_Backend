package com.hamsoo.bpm.controller.modelmember;

import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ModelMemberController {

    private final ModelMemberService modelMemberService;

    public ModelMemberController(ModelMemberService modelMemberService) {
        this.modelMemberService = modelMemberService;
    }

    @GetMapping("/model-member/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelMemberId(#id,@resourceItemService.MODEL_MEMBERS_LIST)")
    public ResponseEntity<?> getModelMemberById(@PathVariable("id") Long id) {
        ModelMember modelMember = modelMemberService.findByIdOrElseThrow(id);
        ModelMemberDto modelMemberDto = modelMemberService.mapToDto(modelMember);
        return new ResponseEntity<>(modelMemberDto, HttpStatus.OK);
    }

//    @PutMapping(value = "/model-member/online/{username}/{modelVersionId}")
//    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_MEMBERS_LIST)")
//    public ResponseEntity<?> onlineModelMemberMode(@PathVariable("username") String username, @PathVariable Long modelVersionId) {
//        modelMemberService.changeMemberMode(username, modelVersionId, ModelMember.ModeType.ONLINE);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @PutMapping(value = "/model-member/offline/{username}/{modelVersionId}")
//    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_MEMBERS_LIST)")
//    public ResponseEntity<?> offlineModelMemberMode(@PathVariable("username") String username, @PathVariable Long modelVersionId) {
//        modelMemberService.changeMemberMode(username, modelVersionId, ModelMember.ModeType.OFFLINE);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


}
