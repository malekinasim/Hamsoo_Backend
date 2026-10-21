package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityDataDto;
import com.hamsoo.bpm.model.dto.identity.IdentityDataSchema;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.service.api.identity.IdentityDataService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdentityDataServiceImpl implements IdentityDataService {

    private final ModelVersionService modelVersionService;
    private final ProcessCategoryService processCategoryService;
    private final ModelMemberService modelMemberService;
    private final UserService userService;
    private final PostService postService;
    private final UnitService unitService;
    private final JobService jobService;
    private final ModelService modelService;

    public IdentityDataServiceImpl(ModelVersionService modelVersionService,
                                   ProcessCategoryService processCategoryService,
                                   ModelMemberService modelMemberService,
                                   UserService userService,
                                   PostService postService, UnitService unitService, JobService jobService, ModelService modelService) {
        this.modelVersionService = modelVersionService;
        this.processCategoryService = processCategoryService;
        this.modelMemberService = modelMemberService;
        this.userService = userService;
        this.postService = postService;
        this.unitService = unitService;
        this.jobService = jobService;
        this.modelService = modelService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityDataDto updateIdentityDataSchema(Long modelVersionId, IdentityDataDto identityDataDto) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        modelVersionService.checkModelVersionStatus(modelVersion.getId());
        IdentityDataSchema identityDataSchema = new IdentityDataSchema();
        identityDataSchema.setMission(identityDataDto.getMission());
        identityDataSchema.setProcessCategoryId(identityDataDto.getProcessCategoryId());
        identityDataSchema.setScope(identityDataDto.getScope());
        modelVersion.setIdentityDataSchema(identityDataSchema);
        modelVersion = modelVersionService.save(modelVersion);
        modelService.updateCode(modelVersion.getModel().getId(), identityDataDto.getModelCode());
        return mapToIdentityDataDto(modelVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDataDto getIdentityDataSchema(Long modelVersionId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        return mapToIdentityDataDto(modelVersion);
    }

    private IdentityDataDto mapToIdentityDataDto(ModelVersion modelVersion) {
        IdentityDataDto identityDataDto = new IdentityDataDto();
        IdentityDataSchema identityDataSchema = modelVersion.getIdentityDataSchema();
        if (identityDataSchema != null) {
            identityDataDto.setMission(identityDataSchema.getMission());
            if (identityDataSchema.getProcessCategoryId() != null) {
                ProcessCategory processCategory = processCategoryService.findByIdOrElseThrow(identityDataSchema.getProcessCategoryId());
                identityDataDto.setProcessCategoryDto(processCategoryService.mapToDto(processCategory));
            }
            identityDataDto.setScope(identityDataSchema.getScope());
        }
        identityDataDto.setModelName(modelVersion.getModel().getName());
        identityDataDto.setModelCode(modelVersion.getModel().getCode());
        List<ModelMember> modelMemberList = modelMemberService.findByModelVersionIdAndModelRoleCode(modelVersion.getId(), ModelRole.Code.MODEL_OWNER.getCode());
        List<ModelMemberDto> modelMemberDtos = modelMemberList.stream().map(modelMember -> modelMemberService.mapToDto(modelMember)).toList();
        identityDataDto.setOwners(modelMemberDtos);
        return identityDataDto;
    }
}
