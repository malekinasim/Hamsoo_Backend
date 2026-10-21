package com.hamsoo.bpm.service.impl.modelversion;

import com.hamsoo.bpm.service.api.identity.*;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.identity.*;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.MilestoneService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final ModelVersionService modelVersionService;
    private final FilesService filesService;
    private final ModelCommentService modelCommentService;
    private final NotificationService notificationService;
    private final ModelMemberService modelMemberService;
    private final ModelMemberRoleService modelMemberRoleService;
    private final ModelRoleService modelRoleService;
    private final ModelDocumentService modelDocumentService;
    private final IoSupCustService ioSupCustService;
    private final IdentityIoService identityIoService;
    private final IdentityKpiService identityKpiService;
    private final ProcessCategoryModelElementService processCategoryModelElementService;
    private final ModelElementReferenceService modelElementReferenceService;
    private final IdentityRiskService identityRiskService;
    private final IdentityComplicationService identityComplicationService;
    private final IdentityResourceService identityResourceService;

    public MilestoneServiceImpl(ModelVersionService modelVersionService,
                                FilesService filesService,
                                ModelCommentService modelCommentService,
                                NotificationService notificationService,
                                ModelMemberService modelMemberService,
                                ModelMemberRoleService modelMemberRoleService, ModelRoleService modelRoleService,
                                ModelDocumentService modelDocumentService,
                                IoSupCustService ioSupCustService,
                                IdentityIoService identityIoService,
                                IdentityKpiService identityKpiService,
                                ProcessCategoryModelElementService processCategoryModelElementService,
                                ModelElementReferenceService modelElementReferenceService, IdentityRiskService identityRiskService, IdentityComplicationService identityComplicationService, IdentityResourceService identityResourceService) {
        this.modelVersionService = modelVersionService;
        this.filesService = filesService;
        this.modelCommentService = modelCommentService;
        this.notificationService = notificationService;
        this.modelMemberService = modelMemberService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.modelRoleService = modelRoleService;
        this.modelDocumentService = modelDocumentService;
        this.ioSupCustService = ioSupCustService;
        this.identityIoService = identityIoService;
        this.identityKpiService = identityKpiService;
        this.processCategoryModelElementService = processCategoryModelElementService;
        this.modelElementReferenceService = modelElementReferenceService;
        this.identityRiskService = identityRiskService;
        this.identityComplicationService = identityComplicationService;
        this.identityResourceService = identityResourceService;
    }

    /**
     * milestone for :
     * AS-IS -> AS-IS
     * TO-BE -> TO-BE
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion milestone(Long modelVersionId, ModelVersionDto input) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelversion.updateLastVersion");
        }
        if (!modelVersion.getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())) {
            throw new BusinessException("error.modelversion.mustBeDraft");
        }
        modelVersion.setValidTo(LocalDateTime.now().minusSeconds(1));
        modelVersion.setLastVersion(false);
        modelVersion.setMilestoneName(input.getMilestoneName());
        modelVersion = modelVersionService.save(modelVersion);

        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setVersionNum(modelVersion.getVersionNum() + 1);
        modelVersionDto.setValidFrom(LocalDateTime.now());
        modelVersionDto.setAsisTobeCode(modelVersion.getAsisTobe().getCode());
        modelVersionDto.setStatusCode(modelVersion.getStatus().getCode());
        modelVersionDto.setModelId(modelVersion.getModel().getId());
        modelVersionDto.setLastVersion(true);
        if (modelVersion.getXml() != null) {
            modelVersionDto.setXml(filesService.map(modelVersion.getXml()));
        }
        if (modelVersion.getImage() != null) {
            modelVersionDto.setImage(filesService.map(modelVersion.getImage()));
        }
        if (modelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkFlowFile(filesService.map(modelVersion.getWorkFlowFile()));
        }
        //modelVersionDto.setResourceSchemaList(modelVersion.getResourceSchemaList());
        modelVersionDto.setIdentityDataSchema(modelVersion.getIdentityDataSchema());
        modelVersionDto.setParentId(modelVersion.getParent().getId());
        ModelVersion newModelVersion = modelVersionService.create(modelVersionDto);
        modelCommentService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId());
        notificationService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId(), Notification.EntityType.MODEL_VERSION.getValue());
        modelMemberService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId());
        modelDocumentService.copyModelDocumentsIfExist(newModelVersion.getId(), modelVersion.getId());
        identityIoService.copyIoIdentityAndIoSupCust(modelVersion.getId(), newModelVersion.getId());
        ioSupCustService.updateInternalSupCust(newModelVersion.getId(), modelVersion.getId());

        identityKpiService.copyIdentityKpi(modelVersion.getId(), newModelVersion.getId());
        identityResourceService.copyIdentityResource(modelVersion.getId(), newModelVersion.getId());
        identityComplicationService.copyIdentityComplication(modelVersion.getId(), newModelVersion.getId());
        identityRiskService.copyIdentityRisk(modelVersion.getId(), newModelVersion.getId());

        if (newModelVersion.getModel().getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            processCategoryModelElementService.copyProcessCategoryModelElementsIfExist(modelVersion.getId(), newModelVersion.getId());
        }
        modelElementReferenceService.copyModelElementRefrenceIfExist(modelVersion.getId(), newModelVersion.getId());
        modelElementReferenceService.updateInternalReferenceIdWithLock(newModelVersion.getId(), modelVersion.getId());
        return newModelVersion;
    }

    /**
     * milestone for :
     * AS-IS -> TO-BE
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion createTobeFromAsis(Long modelVersionId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);

        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelversion.updateLastVersion");
        }
        if (!modelVersion.getAsisTobe().getCode().equals(ModelVersion.AsisTobe.AS_IS.getCode())) {
            throw new BusinessException("error.modelversion.mustBeAsis");
        }
        Optional<ModelVersion> tobe = modelVersionService.findByModelIdAndAsisTobeCode(modelVersion.getModel().getId(),
                ModelVersion.AsisTobe.TO_BE.getCode());
        if (tobe.isPresent()) {
            throw new BusinessException("error.modelversion.modelHasTobeAlready");
        }
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setVersionNum(1);
        modelVersionDto.setValidFrom(LocalDateTime.now());
        modelVersionDto.setAsisTobeCode(ModelVersion.AsisTobe.TO_BE.getCode());
        modelVersionDto.setStatusCode(ModelVersion.Status.DRAFT.getCode());
        modelVersionDto.setModelId(modelVersion.getModel().getId());
        modelVersionDto.setLastVersion(true);
        if (modelVersion.getXml() != null) {
            modelVersionDto.setXml(filesService.map(modelVersion.getXml()));
        }
        if (modelVersion.getImage() != null) {
            modelVersionDto.setImage(filesService.map(modelVersion.getImage()));
        }
        if (modelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkFlowFile(filesService.map(modelVersion.getWorkFlowFile()));
        }
        // modelVersionDto.setResourceSchemaList(modelVersion.getResourceSchemaList());
        modelVersionDto.setIdentityDataSchema(modelVersion.getIdentityDataSchema());
        ModelVersion newModelVersion = modelVersionService.create(modelVersionDto);
        identityIoService.copyIoIdentityAndIoSupCust(modelVersion.getId(), newModelVersion.getId());

        identityKpiService.copyIdentityKpi(modelVersion.getId(), newModelVersion.getId());
        identityComplicationService.copyIdentityComplication(modelVersion.getId(), newModelVersion.getId());
        identityRiskService.copyIdentityRisk(modelVersion.getId(), newModelVersion.getId());
        identityResourceService.copyIdentityResource(modelVersion.getId(), newModelVersion.getId());

        if (newModelVersion.getModel().getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            processCategoryModelElementService.copyProcessCategoryModelElementsIfExist(modelVersion.getId(), newModelVersion.getId());
        }
        modelElementReferenceService.copyModelElementRefrenceIfExist(modelVersion.getId(), newModelVersion.getId());
        List<ModelMember> modelMemberList = modelMemberService.findByModelVersionIdAndModelRoleCode(modelVersion.getId(), ModelRole.Code.MODEL_OWNER.getCode());
        if (CollectionUtils.isEmpty(modelMemberList)) {
            throw new BusinessException("error.modelversion.modelOwnerNotExist");
        }
        for (ModelMember modelMember : modelMemberList) {
            modelMemberRoleService.create(createModelMemberDto(newModelVersion.getId(), modelMember.getMemberId(), modelMember.getMemberType().getCode()));
        }
        return newModelVersion;
    }

    private ModelMemberRoleDto createModelMemberDto(Long modelVersionId, Long memberId, String memberType) {
        ModelMemberRoleDto modelMemberRoleDto = new ModelMemberRoleDto();
        modelMemberRoleDto.setModelVersionId(modelVersionId);
        modelMemberRoleDto.setMemberId(memberId);
        modelMemberRoleDto.setMemberType(memberType);
        modelMemberRoleDto.setModelRoleId(modelRoleService.findByCodeOrElseThrow(ModelRole.Code.MODEL_OWNER.getCode()).getId());
        return modelMemberRoleDto;
    }

    /**
     * Operate the process(model)
     * TO-BE -> AS-IS
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion operateTobeModel(Long modelVersionId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        validateOperateTobeModel(modelVersion);
        Optional<ModelVersion> lastDisabledAsisOptional = disableLastAsisIfExist(modelVersion.getModel().getId());
        modelVersion.setValidTo(LocalDateTime.now().minusSeconds(1));
        modelVersion.setLastVersion(false);
        modelVersion = modelVersionService.save(modelVersion);

        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setVersionNum(1);
        modelVersionDto.setValidFrom(LocalDateTime.now());
        modelVersionDto.setAsisTobeCode(ModelVersion.AsisTobe.AS_IS.getCode());
        modelVersionDto.setStatusCode(ModelVersion.Status.APPROVE.getCode());
        modelVersionDto.setModelId(modelVersion.getModel().getId());
        modelVersionDto.setLastVersion(true);
        if (modelVersion.getXml() != null) {
            modelVersionDto.setXml(filesService.map(modelVersion.getXml()));
        }
        if (modelVersion.getImage() != null) {
            modelVersionDto.setImage(filesService.map(modelVersion.getImage()));
        }
        if (modelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkFlowFile(filesService.map(modelVersion.getWorkFlowFile()));
        }
        // modelVersionDto.setResourceSchemaList(modelVersion.getResourceSchemaList());
        modelVersionDto.setIdentityDataSchema(modelVersion.getIdentityDataSchema());
        ModelVersion newModelVersion = modelVersionService.create(modelVersionDto);
        modelCommentService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId());
        notificationService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId(), Notification.EntityType.MODEL_VERSION.getValue());
        modelMemberService.updateModelVersionId(newModelVersion.getId(), modelVersion.getId());
        modelDocumentService.copyModelDocumentsIfExist(newModelVersion.getId(), modelVersion.getId());
        identityIoService.copyIoIdentityAndIoSupCust(modelVersion.getId(), newModelVersion.getId());
        ioSupCustService.updateInternalSupCust(newModelVersion.getId(), modelVersion.getId());

        identityKpiService.copyIdentityKpi(modelVersion.getId(), newModelVersion.getId());
        identityComplicationService.copyIdentityComplication(modelVersion.getId(), newModelVersion.getId());
        identityRiskService.copyIdentityRisk(modelVersion.getId(), newModelVersion.getId());
        identityResourceService.copyIdentityResource(modelVersion.getId(), newModelVersion.getId());

        if (newModelVersion.getModel().getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            processCategoryModelElementService.copyProcessCategoryModelElementsIfExist(modelVersion.getId(), newModelVersion.getId());
        }
        modelElementReferenceService.copyModelElementRefrenceIfExist(modelVersion.getId(), newModelVersion.getId());
        if (lastDisabledAsisOptional.isPresent()) {
            ModelVersion lastDisabledAsis = lastDisabledAsisOptional.get();
            ioSupCustService.checkUpdateInternalSupCust(newModelVersion, lastDisabledAsis);
            ioSupCustService.updateInternalSupCust(newModelVersion.getId(), lastDisabledAsis.getId());
            modelElementReferenceService.updateInternalReferenceIdWithLock(newModelVersion.getId(), lastDisabledAsis.getId());
        }
        return newModelVersion;
    }

    private void validateOperateTobeModel(ModelVersion modelVersion) {
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelversion.updateLastVersion");
        }
        if (!modelVersion.getAsisTobe().getCode().equals(ModelVersion.AsisTobe.TO_BE.getCode())) {
            throw new BusinessException("error.modelversion.mustBeTobe");
        }
        if (!modelVersion.getStatus().getCode().equals(ModelVersion.Status.APPROVE.getCode())) {
            throw new BusinessException("error.modelversion.mustBeApproved");
        }
    }

    private Optional<ModelVersion> disableLastAsisIfExist(Long modelId) {
        Optional<ModelVersion> optionalAsis = modelVersionService.findByModelIdAndAsisTobeCode(modelId, ModelVersion.AsisTobe.AS_IS.getCode());
        if (optionalAsis.isPresent()) {
            ModelVersion asis = optionalAsis.get();
            asis.setValidTo(LocalDateTime.now().minusSeconds(1));
            asis.setLastVersion(false);
            asis = modelVersionService.save(asis);
            return Optional.of(asis);
        }
        return optionalAsis;
    }

    /**
     * restore to old version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion restoreToOldVersion(Long oldModelVersionId) {
        ModelVersion oldModelVersion = modelVersionService.findByIdOrElseThrow(oldModelVersionId);
        if (oldModelVersion.getLastVersion()) {
            throw new BusinessException("error.modelversion.modelVersionIsLastVersion");
        }
        ModelVersion lastModelVersion = modelVersionService.getLastVersion(oldModelVersion.getAsisTobe().getCode(),
                oldModelVersion.getModel().getId());
        if (lastModelVersion == null) {
            throw new BusinessException("error.modelversion.lastVersionNotFound");
        }
        if (!lastModelVersion.getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())) {
            throw new BusinessException("error.modelversion.mustBeDraft");
        }
        if (lastModelVersion.getParent() == null || oldModelVersion.getParent() == null) {
            throw new BusinessException("error.modelversion.parentNotFound");
        }
        if (!lastModelVersion.getParent().getId().equals(oldModelVersion.getParent().getId())) {
            throw new BusinessException("error.modelversion.idIsNotValid");
        }
        lastModelVersion.setValidTo(LocalDateTime.now().minusSeconds(1));
        lastModelVersion.setLastVersion(false);


        ULocale locale = new ULocale("fa_IR@calendar=persian");

        Calendar calendar = Calendar.getInstance(locale);
        DateFormat df = DateFormat.getDateInstance(DateFormat.DATE_FIELD, locale);

        lastModelVersion.setMilestoneName(String.join("_", ResourceBundleUtils.getMessage("model.version.name"),
                StringUtils.leftPad(oldModelVersion.getVersionNum().toString(), 3, "0"),
                df.format(calendar.getTime())
        ));

        lastModelVersion = modelVersionService.save(lastModelVersion);

        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setVersionNum(lastModelVersion.getVersionNum() + 1);
        modelVersionDto.setValidFrom(LocalDateTime.now());
        modelVersionDto.setAsisTobeCode(lastModelVersion.getAsisTobe().getCode());
        modelVersionDto.setStatusCode(lastModelVersion.getStatus().getCode());
        modelVersionDto.setModelId(lastModelVersion.getModel().getId());
        modelVersionDto.setLastVersion(true);
        if (oldModelVersion.getXml() != null) {
            modelVersionDto.setXml(filesService.map(oldModelVersion.getXml()));
        }
        if (oldModelVersion.getImage() != null) {
            modelVersionDto.setImage(filesService.map(oldModelVersion.getImage()));
        }
        if (oldModelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkFlowFile(filesService.map(oldModelVersion.getWorkFlowFile()));
        }
        // modelVersionDto.setResourceSchemaList(oldModelVersion.getResourceSchemaList());
        modelVersionDto.setIdentityDataSchema(oldModelVersion.getIdentityDataSchema());
        modelVersionDto.setParentId(lastModelVersion.getParent().getId());
        ModelVersion newModelVersion = modelVersionService.create(modelVersionDto);
        modelCommentService.updateModelVersionId(newModelVersion.getId(), lastModelVersion.getId());
        notificationService.updateModelVersionId(newModelVersion.getId(), lastModelVersion.getId(), Notification.EntityType.MODEL_VERSION.getValue());
        modelMemberService.updateModelVersionId(newModelVersion.getId(), lastModelVersion.getId());
        modelDocumentService.copyModelDocumentsIfExist(newModelVersion.getId(), oldModelVersion.getId());
        identityIoService.copyIoIdentityAndIoSupCust(oldModelVersion.getId(), newModelVersion.getId());
        ioSupCustService.checkUpdateInternalSupCust(newModelVersion, lastModelVersion);
        ioSupCustService.updateInternalSupCust(newModelVersion.getId(), lastModelVersion.getId());

        identityKpiService.copyIdentityKpi(oldModelVersion.getId(), newModelVersion.getId());
        identityComplicationService.copyIdentityComplication(oldModelVersion.getId(), newModelVersion.getId());
        identityRiskService.copyIdentityRisk(oldModelVersion.getId(), newModelVersion.getId());
        identityResourceService.copyIdentityResource(oldModelVersion.getId(), newModelVersion.getId());
        if (newModelVersion.getModel().getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            processCategoryModelElementService.copyProcessCategoryModelElementsIfExist(oldModelVersion.getId(), newModelVersion.getId());
        }
        modelElementReferenceService.copyModelElementRefrenceIfExist(oldModelVersion.getId(), newModelVersion.getId());
        modelElementReferenceService.updateInternalReferenceIdWithLock(newModelVersion.getId(), lastModelVersion.getId());
        return newModelVersion;
    }


}
