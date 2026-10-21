package com.hamsoo.bpm.common.config.security.custompremission;


import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;
import com.hamsoo.bpm.model.dto.resourceitem.ResourceItemDto;
import com.hamsoo.bpm.model.entity.identity.*;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.identity.*;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomMethodSecurityExpression
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {


    ModelMemberService modelMemberService;
    ModelDocumentService modelDocumentService;
    IdentityIoService identityIoService;
    ModelCommentService modelCommentService;
    IoSupCustService ioSupCustService;
    IdentityKpiService identityKpiService;
    ModelService modelService;
    ModelVersionService modelVersionService;
    ModelRoleService modelRoleService;
    ModelElementReferenceService modelElementReferenceService;

    IdentityRiskService identityRiskService;
    IdentityComplicationService identityComplicationService;

    IdentityResourceService identityResourceService;
    private Object filterObject;
    private Object returnObject;


    public CustomMethodSecurityExpression(Authentication authentication) {
        super(authentication);
    }

    public boolean hasAnyResourceByModelVersionId(Long modelVersionId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            if (hasAnyAuthority(resourceAuthorities)) return true;
            else if (modelVersionId == null) return false;
            else {
                List<ModelMember> modelMembers = modelMemberService.
                        getUserModelMember(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelVersionId);
                if (modelMembers.isEmpty()) return false;
                for (String resourceName : resourceAuthorities) {
                    List<ModelRole> userModelRoles = new ArrayList<>();

                    modelMembers.forEach(modelMember ->
                            userModelRoles.addAll(modelMember.getModelMemberRoleList().stream()
                                    .map(ModelMemberRole::getModelRole).toList())
                    );
                    List<String> userModelResources = new ArrayList<>();
                    userModelRoles.stream().map(role -> modelRoleService.mapToDto(role)).forEach(role -> {
                        userModelResources.addAll(role.getGrantedResourceDtos().stream()
                                .map(GrantedResourceDto::getResourceItemDto)
                                .map(ResourceItemDto::getName)
                                .toList());

                    });
                    if (userModelResources.stream().anyMatch(userRole -> userRole.equals(resourceName)))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyResourceByModelId(Long modelId, String... resourceAuthorities) {
        if (hasAnyAuthority(resourceAuthorities)) return true;
        if (modelId == null) return false;
        else {
            Model model = modelService.findByIdOrElseThrow(modelId);
            ModelDto modelDto = modelService.mapToDto(model);
            boolean isAsIsMember = (modelDto.getLastAsisVersion() == null || hasAnyResourceByModelVersionId(modelDto.getLastAsisVersion().getId(), resourceAuthorities));
            boolean isToBeMember = (modelDto.getLastTobeVersion() == null || hasAnyResourceByModelVersionId(modelDto.getLastTobeVersion().getId(), resourceAuthorities));
            return isAsIsMember || isToBeMember;
        }

    }

    public boolean hasAnyResource(String... resourceAuthorities) {
        if (isAuthenticated()) {
            if (hasAnyAuthority(resourceAuthorities)) return true;
            else {
                List<ModelRoleDto> modelRoleDtos = modelRoleService.findAllUserModelRoles();
                if (modelRoleDtos.isEmpty()) return false;
                for (String modelRoleName : resourceAuthorities) {
                    List<String> userModelResources = new ArrayList<>();
                    modelRoleDtos.forEach(role -> {
                        userModelResources.addAll(role.getGrantedResourceDtos().stream()
                                .map(GrantedResourceDto::getResourceItemDto)
                                .map(ResourceItemDto::getName).toList());

                    });
                    if (userModelResources.stream().anyMatch(userRole -> userRole.equals(modelRoleName)))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyResourceByModelCommentId(Long id, String... resourceAuthorities) {
        if (isAuthenticated()) {
            ModelComment modelComment = modelCommentService.findByIdOrElseThrow(id);
            return hasAnyResourceByModelVersionId(modelComment.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByModelDocumentId(Long modelDocumentId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            ModelDocument modelDocument = modelDocumentService.findByIdOrElseThrow(modelDocumentId);
            return hasAnyResourceByModelVersionId(modelDocument.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByModelApproverId(Long modelApproverId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            Optional<ModelVersion> modelVersion = modelVersionService.findModelVersionOfModelApprover(modelApproverId);
            if (modelVersion.isPresent())
                return hasAnyResourceByModelVersionId(modelVersion.get().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIdentityResourceId(Long identityResourceId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IdentityResource identityResource = identityResourceService.findByIdOrElseThrow(identityResourceId);
            return hasAnyResourceByModelVersionId(identityResource.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByModelElementReferenceId(Long modelElementReferenceId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            Optional<ModelVersion> modelVersion = modelVersionService.findModelVersionOfModelElementReferenceId(modelElementReferenceId);
            if (modelVersion.isPresent())
                return hasAnyResourceByModelVersionId(modelVersion.get().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByProcessCategoryModelElementId(Long id, String... resourceAuthorities) {
        if (isAuthenticated()) {
            ModelElementRefrence modelElementRefrence = modelVersionService.findModelVersionOfProcessCategoryModelElement(id);
            return hasAnyResourceByModelVersionId(modelElementRefrence.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByModelMemberId(Long id, String... resourceAuthorities) {
        if (isAuthenticated()) {
            ModelMember modelMember = modelMemberService.findByIdOrElseThrow(id);
            return hasAnyResourceByModelVersionId(modelMember.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByModelMemberRoleId(Long id, String... resourceAuthorities) {
        if (isAuthenticated()) {
            Optional<ModelVersion> modelVersion = modelVersionService.findModelVersionOfModelMemberRole(id);
            if (modelVersion.isPresent())
                return hasAnyResourceByModelVersionId(modelVersion.get().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIdentityRiskId(Long identityRiskId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IdentityRisk identityRisk = identityRiskService.findByIdOrElseThrow(identityRiskId);
            return hasAnyResourceByModelVersionId(identityRisk.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIdentityComplicationId(Long identityComplicationId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IdentityComplication identityComplication = identityComplicationService.findByIdOrElseThrow(identityComplicationId);
            return hasAnyResourceByModelVersionId(identityComplication.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIdentityKpiId(Long identityKpiId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IdentityKpi identityKpi = identityKpiService.findByIdOrElseThrow(identityKpiId);
            return hasAnyResourceByModelVersionId(identityKpi.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIdentityIoId(Long identityIoId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IdentityIo identityIo = identityIoService.findByIdOrElseThrow(identityIoId);
            return hasAnyResourceByModelVersionId(identityIo.getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }

    public boolean hasAnyResourceByIoSupCustId(Long ioSupCustId, String... resourceAuthorities) {
        if (isAuthenticated()) {
            IoSupCust ioSupCust = ioSupCustService.findByIdOrElseThrow(ioSupCustId);
            return hasAnyResourceByModelVersionId(ioSupCust.getIdentityIo().getModelVersion().getId(), resourceAuthorities);
        }
        return false;
    }


    public boolean isAnyRoleMemberByModelVersionId(Long modelVersionId, String... authorities) {
        if (isAuthenticated()) {
            if (hasAnyAuthority(authorities)) return true;
            else if (modelVersionId == null) return false;
            else {
                List<ModelMember> modelMembers = modelMemberService.getUserModelMember(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelVersionId);
                if (modelMembers.isEmpty()) return false;
                for (String modelRoleName : authorities) {
                    List<String> userRoles = new ArrayList<>();
                    modelMembers.forEach(modelMember -> {
                        userRoles.addAll(modelMember.getModelMemberRoleList().stream().map(ModelMemberRole::getModelRole)
                                .map(ModelRole::getCode).toList());
                    });
                    if (userRoles.stream().anyMatch(userRole -> userRole.equals(modelRoleName)))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean isModelMemberByModelVersion(Long modelVersionId) {
        if (modelVersionId == null) {
            return false;
        }
        if (isAuthenticated()) {
            return modelMemberService.isUserMemberOfModel(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelVersionId);
        }
        return false;
    }

    public boolean isAnyRoleMemberByModelId(Long modelId, String... authorities) {
        if (hasAnyAuthority(authorities)) return true;
        if (modelId == null) return false;
        else {
            Model model = modelService.findByIdOrElseThrow(modelId);
            ModelDto modelDto = modelService.mapToDto(model);
            boolean isAsIsMember = (modelDto.getLastAsisVersion() == null || isAnyRoleMemberByModelVersionId(modelDto.getLastAsisVersion().getId(), authorities));
            boolean isToBeMember = (modelDto.getLastTobeVersion() == null || isAnyRoleMemberByModelVersionId(modelDto.getLastTobeVersion().getId(), authorities));
            return isAsIsMember || isToBeMember;
        }

    }

    public boolean isModelMemberByModelId(Long modelId) {
        if (isAuthenticated()) {
            Model model = modelService.findByIdOrElseThrow(modelId);
            ModelDto modelDto = modelService.mapToDto(model);
            boolean isAsIsMember = (modelDto.getLastAsisVersion() == null || isModelMemberByModelVersion(modelDto.getLastAsisVersion().getId()));
            boolean isToBeMember = (modelDto.getLastTobeVersion() == null || isModelMemberByModelVersion(modelDto.getLastTobeVersion().getId()));
            return isAsIsMember || isToBeMember;
        }
        return false;
    }

    public boolean isModelMemberByModelDocumentId(Long modelDocumentId) {
        if (isAuthenticated()) {
            ModelDocument modelDocument = modelDocumentService.findByIdOrElseThrow(modelDocumentId);
            return isModelMemberByModelVersion(modelDocument.getModelVersion().getId());
        }
        return false;
    }

    public boolean isModelMemberByIdentityIoId(Long identityIoId) {
        if (isAuthenticated()) {
            IdentityIo identityIo = identityIoService.findByIdOrElseThrow(identityIoId);
            return isModelMemberByModelVersion(identityIo.getModelVersion().getId());
        }
        return false;
    }

    public boolean isModelMemberByIoSupCustId(Long ioSupCustId) {
        if (isAuthenticated()) {
            IoSupCust ioSupCust = ioSupCustService.findByIdOrElseThrow(ioSupCustId);
            return isModelMemberByModelVersion(ioSupCust.getIdentityIo().getModelVersion().getId());
        }
        return false;
    }

    public boolean isModelMemberByIdentityKpiId(Long identityKpiId) {
        if (isAuthenticated()) {
            IdentityKpi identityKpi = identityKpiService.findByIdOrElseThrow(identityKpiId);
            return isModelMemberByModelVersion(identityKpi.getModelVersion().getId());
        }
        return false;
    }

    public boolean isModelMemberByModelElementReferenceId(Long modelElementReferenceId) {
        if (isAuthenticated()) {
            ModelElementRefrence modelElementRefrence = modelElementReferenceService.findByIdOrElseThrow(modelElementReferenceId);
            return isModelMemberByModelVersion(modelElementRefrence.getModelVersion().getId());
        }
        return false;
    }


    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    public void setModelMemberService(ModelMemberService modelMemberService) {
        this.modelMemberService = modelMemberService;
    }

    public void setModelDocumentService(ModelDocumentService modelDocumentService) {
        this.modelDocumentService = modelDocumentService;
    }

    public void setIdentityIoService(IdentityIoService identityIoService) {
        this.identityIoService = identityIoService;
    }

    public void setIoSupCustService(IoSupCustService ioSupCustService) {
        this.ioSupCustService = ioSupCustService;
    }

    public void setIdentityKpiService(IdentityKpiService identityKpiService) {
        this.identityKpiService = identityKpiService;
    }

    public void setModelElementReferenceService(ModelElementReferenceService modelElementReferenceService) {
        this.modelElementReferenceService = modelElementReferenceService;
    }


    public void setIdentityComplicationService(IdentityComplicationService identityComplicationService) {
        this.identityComplicationService = identityComplicationService;
    }

    public void setIdentityResourceService(IdentityResourceService identityResourceService) {
        this.identityResourceService = identityResourceService;
    }

    public void setIdentityRiskService(IdentityRiskService identityRiskService) {
        this.identityRiskService = identityRiskService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setModelRoleService(ModelRoleService modelRoleService) {
        this.modelRoleService = modelRoleService;
    }

    public void setModelCommentService(ModelCommentService modelCommentService) {
        this.modelCommentService = modelCommentService;
    }

    public void setModelVersionService(ModelVersionService modelVersionService) {
        this.modelVersionService = modelVersionService;
    }
}
