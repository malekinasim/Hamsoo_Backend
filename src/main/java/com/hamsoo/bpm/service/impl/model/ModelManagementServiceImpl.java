package com.hamsoo.bpm.service.impl.model;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.BpmnHelperService;
import com.hamsoo.bpm.common.utils.StringUtil;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.model.ModelManagementDto;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryDto;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryModelElementDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.processcategory.ProcessCategory;
import com.hamsoo.bpm.service.api.model.ModelManagementService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryService;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ModelManagementServiceImpl implements ModelManagementService {

    private final ModelService modelService;
    private final ModelVersionService modelVersionService;
    private final OrganPlanService organPlanService;
    private final ModelMemberRoleService modelMemberRoleService;
    private final ModelRoleService modelRoleService;
    private final ProcessCategoryService processCategoryService;
    private final ProcessCategoryModelElementService processCategoryModelElementService;

    public ModelManagementServiceImpl(ModelService modelService,
                                      ModelVersionService modelVersionService,
                                      OrganPlanService organPlanService,
                                      ModelMemberRoleService modelMemberRoleService, ModelRoleService modelRoleService,
                                      ProcessCategoryService processCategoryService,
                                      ProcessCategoryModelElementService processCategoryModelElementService) {
        this.modelService = modelService;
        this.modelVersionService = modelVersionService;
        this.organPlanService = organPlanService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.modelRoleService = modelRoleService;
        this.processCategoryService = processCategoryService;
        this.processCategoryModelElementService = processCategoryModelElementService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelManagementDto create(ModelManagementDto modelManagementDto, MultipartFile xml, MultipartFile image) {
        Assert.notNull(modelManagementDto, "modelManagementDto is null");
        organPlanService.CheckOrganModelCountLimitBaseOnLastOrganPlan(SecurityUtils.getCurrentOrganIdOrElseThrow());
        Model model = modelService.create(mapToModelDto(modelManagementDto));
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setVersionNum(1);
        modelVersionDto.setValidFrom(LocalDateTime.now());
        modelVersionDto.setAsisTobeCode(modelManagementDto.getAsisTobe().getCode());
        modelVersionDto.setStatusCode(ModelVersion.Status.DRAFT.getCode());
        modelVersionDto.setModelId(model.getId());
        modelVersionDto.setLastVersion(true);
        ModelVersion modelVersion = modelVersionService.create(modelVersionDto);
        modelVersionService.updateXmlAndImage(xml, image, modelVersion.getId());
        if (model.getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            syncProcessCategoryAndProcessCategoryModelElement(xml, modelVersion.getId());
        }
        // ModelMemberRole modelMemberRole = modelMemberRoleService.create(createModelMemberDto(modelVersion.getId()));
        // return mapToModelManagementDto(model, modelVersion, modelMemberRole);
        return mapToModelManagementDto(model, modelVersion);
    }

    private ModelDto mapToModelDto(ModelManagementDto modelManagementDto) {
        ModelDto modelDto = new ModelDto();
        modelDto.setName(modelManagementDto.getName());
//        modelDto.setCode(modelManagementDto.getCode());
        modelDto.setModelTypeCode(modelManagementDto.getModelTypeCode());
        modelDto.setResponsibleUnitId(modelManagementDto.getResponsibleUnitId());
        return modelDto;
    }

    private ModelMemberRoleDto createModelMemberDto(Long modelVersionId) {
        ModelMemberRoleDto modelMemberRoleDto = new ModelMemberRoleDto();
        modelMemberRoleDto.setFromDate(LocalDateTime.now());
        modelMemberRoleDto.setModelVersionId(modelVersionId);
        modelMemberRoleDto.setMemberId(SecurityUtils.getCurrentUserOrElseThrow().getId());
        modelMemberRoleDto.setMemberType(ModelMember.MemberType.USER.getCode());
        modelMemberRoleDto.setModelRoleId(modelRoleService.findByCodeOrElseThrow(ModelRole.Code.MODEL_OWNER.getCode()).getId());
        //TODO get collector list of model and add collector as model colelctor to model
        return modelMemberRoleDto;
    }

    private ModelManagementDto mapToModelManagementDto(Model model, ModelVersion modelVersion, ModelMemberRole modelMemberRole) {
        ModelManagementDto modelManagementDto = new ModelManagementDto();
        modelManagementDto.setModelDto(modelService.mapToDto(model));
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        modelManagementDto.setModelVersionDtoList(Collections.singletonList(modelVersionDto));
        ModelMemberRoleDto modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
        modelManagementDto.setModelMemberDtoList(Collections.singletonList(modelMemberRoleDto));
        return modelManagementDto;
    }

    private ModelManagementDto mapToModelManagementDto(Model model, ModelVersion modelVersion) {
        ModelManagementDto modelManagementDto = new ModelManagementDto();
        modelManagementDto.setModelDto(modelService.mapToDto(model));
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        modelManagementDto.setModelVersionDtoList(Collections.singletonList(modelVersionDto));
//        ModelMemberRoleDto modelMemberRoleDto = modelMemberRoleService.mapToDto(modelMemberRole);
//        modelManagementDto.setModelMemberDtoList(Collections.singletonList(modelMemberRoleDto));
        return modelManagementDto;
    }

    private void syncProcessCategoryAndProcessCategoryModelElement(MultipartFile xml, Long modelVersionId) {
        List<Lane> lanes;
        try {
            BpmnHelperService bpmnHelperService = new BpmnHelperService(xml.getInputStream());
            lanes = bpmnHelperService.getLanes();
        } catch (IOException e) {
            throw new BusinessException("error.modelManagement.errorInReadingBpmn");
        }
        if (CollectionUtils.isEmpty(lanes)) {
            return;
        }
        //validate lane
        lanes.forEach(lane -> {
            if (!StringUtils.hasText(lane.getId()) || !StringUtils.hasText(lane.getName())) {
                throw new BusinessException("error.modelManagement.idOrNameOfLaneIsEmpty");
            }
        });
        lanes.forEach(lane -> {
            String laneName = StringUtil.replaceSpecialCharacter(lane.getName());
            Optional<ProcessCategory> optionalProcessCategory =
                    processCategoryService.findActiveByOrganIdAndName(SecurityUtils.getCurrentOrganIdOrElseThrow(), laneName);
            Long processCategoryId;
            if (optionalProcessCategory.isPresent()) {
                processCategoryId = optionalProcessCategory.get().getId();
            } else {
                ProcessCategoryDto processCategoryDto = new ProcessCategoryDto();
                processCategoryDto.setName(laneName);
                ProcessCategory processCategory = processCategoryService.create(processCategoryDto);
                processCategoryId = processCategory.getId();
            }
            ProcessCategoryModelElementDto processCategoryModelElementDto = new ProcessCategoryModelElementDto();
            processCategoryModelElementDto.setElementId(lane.getId());
            processCategoryModelElementDto.setModelVersionId(modelVersionId);
            processCategoryModelElementDto.setProcessCategoryId(processCategoryId);
            processCategoryModelElementService.create(processCategoryModelElementDto);
        });
    }

}
