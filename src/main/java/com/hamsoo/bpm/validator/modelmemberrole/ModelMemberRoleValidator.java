package com.hamsoo.bpm.validator.modelmemberrole;

import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;

@Component
public class  ModelMemberRoleValidator extends HamsooValidator {

    private final ModelMemberRoleService modelMemberRoleService;

    private final UserService userService;
    private final PostService postService;
    private final JobService jobService;
    private final UnitService unitService;

    public ModelMemberRoleValidator(ModelMemberRoleService modelMemberRoleService,
                                    UserService userService, PostService postService, JobService jobService, UnitService unitService) {

        this.modelMemberRoleService = modelMemberRoleService;
        this.userService = userService;
        this.postService = postService;
        this.jobService = jobService;
        this.unitService = unitService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ModelMemberRoleDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
     super.validate(target, errors);
        ModelMemberRoleDto dto = (ModelMemberRoleDto) target;

        if (dto.getId() == null) {
            //create mode
            if (dto.getModelVersionId() == null) {
                errors.rejectValue("modelVersionId", "", "error.modelMemberRole.modelVersionIdIsNotValid");
            }
            if (dto.getMemberId() == null) {
                errors.rejectValue("memberId", "", "error.modelMemberRole.memberIdIsNotValid");
            }
            if (dto.getMemberType() == null || Arrays.stream(ModelMember.MemberType.values()).map(ModelMember.MemberType::getCode)
                    .anyMatch(code-> code.equals(dto.getMemberType()))) {
                errors.rejectValue("memberType", "", "error.modelMemberRole.memberTypeIsNotValid");
            }
            if (dto.getModelRoleId() == null) {
                errors.rejectValue("modelRoleId", "", "error.modelMemberRole.modelRoleIdIsNotValid");
            }
        }



         // String memberName;
        if(dto.getMemberType().equals(ModelMember.MemberType.USER.getCode())){
            try {
               User u= userService.findByIdOrElseThrow(dto.getMemberId());
              //  memberName=u.getFullName();
            }catch (Exception e){
                errors.rejectValue("modelRoleId", "", "error.modelMemberRole.userNotFount");
            }
        }else if(dto.getMemberType().equals(ModelMember.MemberType.POST.getCode())){
            try {
                Post p=postService.findByIdOrElseThrow(dto.getMemberId());
               // memberName=p.getName();
            }catch (Exception e){
                errors.rejectValue("modelRoleId", "", "error.modelMemberRole.postNotFount");
            }
        }else if(dto.getMemberType().equals(ModelMember.MemberType.JOB.getCode())){
            try {
              Job job= jobService.findByIdOrElseThrow(dto.getMemberId());
               // memberName=job.getName();
            }catch (Exception e){
                errors.rejectValue("modelRoleId", "", "error.modelMemberRole.jobNotFount");
            }
        }else if(dto.getMemberType().equals(ModelMember.MemberType.UNIT.getCode())){
            try {
                Unit unit= unitService.findByIdOrElseThrow(dto.getMemberId());
             //   memberName=unit.getName();
            }catch (Exception e){
                errors.rejectValue("modelRoleId", "", "error.modelMemberRole.unitNotFount");
            }
        }

        //TODO need check role for each member not duplicated

        List<ModelMemberRole> modelMemberRoles = modelMemberRoleService.findByModelVersionIdAndMemberIdAndMemberTypeAndRoleIdAndStatus(
                dto.getModelVersionId(),
                dto.getMemberId(),
                dto.getMemberType(),
                dto.getModelRoleId(),
                Status.ACTIVE.name());
        if (!CollectionUtils.isEmpty(modelMemberRoles)) {
            errors.rejectValue("modelRoleId", "", "error.modelMember.modelMemberIsDuplicate");
        }

//TODO need check role for each user not duplicate specially for post job and unit
//        List<User> userList=new ArrayList<>();
//        if(dto.getMemberType().equals(ModelMember.MemberType.POST.getCode()))
//           userList.addAll(userService.findAllPostMember(dto.getMemberId()));
//
//        if(dto.getMemberType().equals(ModelMember.MemberType.JOB.getCode()))
//            userList.addAll(userService.findAllJobMember(dto.getMemberId()));
//
//
//        if(dto.getMemberType().equals(ModelMember.MemberType.UNIT.getCode()))
//            userList.addAll(userService.findAllUnitMember(dto.getMemberId()));
//
//         for(User u : userList) {
//              modelMemberRoles = modelMemberRoleService.findByModelVersionIdAndMemberIdAndMemberTypeAndRoleIdAndStatus(
//                     dto.getModelVersionId(),
//                     u.getId(),
//                     ModelMember.MemberType.USER.getCode(),
//                     dto.getModelRoleId(),
//                     Status.ACTIVE.name());
//             if (!CollectionUtils.isEmpty(modelMemberRoles)) {
//                 errors.rejectValue("modelRoleId", "",new String []{ u.getId().toString(),
//                         dto.getMemberType()
//
//                 },"error.modelMember.modelMemberForMemberTypeIsDuplicate");
//             }
//         }


    }
}
