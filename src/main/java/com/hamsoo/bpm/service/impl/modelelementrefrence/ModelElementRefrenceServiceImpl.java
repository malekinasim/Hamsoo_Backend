package com.hamsoo.bpm.service.impl.modelelementrefrence;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.identity.IoSupCustDto;
import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.repository.jpa.modelelementrefrence.ModelElementRefrenceRepository;
import org.apache.http.util.Asserts;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelElementRefrenceServiceImpl implements ModelElementReferenceService {
    private final ModelElementRefrenceRepository modelElementRefrenceRepository;
    private final ModelVersionService modelVersionService;
    private final ExternalProcessService externalProcessService;

    private final ModelCommentService modelCommentService;

    private final PostService postService;
    private final CodeCategoryService codeCategoryService;
    private final IoSupCustService ioSupCustService;

    public ModelElementRefrenceServiceImpl(ModelElementRefrenceRepository modelElementRefrenceRepository,
                                           ModelVersionService modelVersionService,
                                           ExternalProcessService externalProcessService,
                                           @Lazy ModelCommentService modelCommentService,
                                           PostService postService, CodeCategoryService codeCategoryService,
                                           IoSupCustService ioSupCustService) {
        this.modelElementRefrenceRepository = modelElementRefrenceRepository;
        this.modelVersionService = modelVersionService;
        this.externalProcessService = externalProcessService;
        this.modelCommentService = modelCommentService;
        this.postService = postService;
        this.codeCategoryService = codeCategoryService;
        this.ioSupCustService = ioSupCustService;
    }

    @Override
    public ModelElementRefrenceRepository getRepository() {
        return modelElementRefrenceRepository;
    }


    @Override
    public ModelElementRefrence findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelDocument.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.modelElementReference.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        ModelElementRefrence modelElementRefrence = findByIdOrElseThrow(id);
        delete(modelElementRefrence);

    }

    @Override
    public ModelElementReferenceDto mapToDto(ModelElementRefrence modelElementReference) {
        Assert.notNull(modelElementReference, "ModelElementRefrence is null");
        modelElementReference = findByIdOrElseThrow(modelElementReference.getId());
        ModelElementReferenceDto modelElementReferenceDto = new ModelElementReferenceDto();
        modelElementReferenceDto.setId(modelElementReference.getId());
        modelElementReferenceDto.setModelReferenceId(modelElementReference.getReferenceId());
        modelElementReferenceDto.setModelVersionId(modelElementReference.getModelVersion().getId());
        modelElementReferenceDto.setParentModelVersionDto(modelVersionService.mapToDto(modelElementReference.getModelVersion()));
        modelElementReferenceDto.setReferenceTypeCode(modelElementReference.getReferenceType().getCode());
        modelElementReferenceDto.setReferenceTypeTitle(modelElementReference.getReferenceType().getFaName());
        //add parent ModelVersion
        if (modelElementReference.getReferenceType().getCode().equals(ModelElementRefrence.ReferenceType.EXTERNAL.getCode())) {
            ExternalProcess externalProcess = externalProcessService.findByIdOrElseThrow(modelElementReference.getReferenceId());
            modelElementReferenceDto.setExternalProcessDto(externalProcessService.mapToDto(externalProcess));
        } else if (modelElementReference.getReferenceType().getCode().equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())) {
            ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelElementReference.getReferenceId());
            modelElementReferenceDto.setReferenceModelVersionDto(modelVersionService.mapToDto(modelVersion));
        } else if (modelElementReference.getReferenceType().getCode().equals(ModelElementRefrence.ReferenceType.COMMENT.getCode())) {
            ModelComment comment = modelCommentService.findByIdOrElseThrow(modelElementReference.getReferenceId());
            modelElementReferenceDto.setModelCommentDto(modelCommentService.mapToDto(comment));
        }

        modelElementReferenceDto.setElementId(modelElementReference.getElementId());
        return modelElementReferenceDto;
    }

    @Override
    public ModelElementReferenceDto mapToDtoWithoutReferenceInfo(ModelElementRefrence modelElementReference) {
        Assert.notNull(modelElementReference, "ModelElementRefrence is null");
        modelElementReference = findByIdOrElseThrow(modelElementReference.getId());
        ModelElementReferenceDto modelElementReferenceDto = new ModelElementReferenceDto();
        modelElementReferenceDto.setId(modelElementReference.getId());
        modelElementReferenceDto.setModelReferenceId(modelElementReference.getReferenceId());
        modelElementReferenceDto.setModelVersionId(modelElementReference.getModelVersion().getId());
        modelElementReferenceDto.setParentModelVersionDto(modelVersionService.mapToDto(modelElementReference.getModelVersion()));
        modelElementReferenceDto.setReferenceTypeCode(modelElementReference.getReferenceType().getCode());
        modelElementReferenceDto.setReferenceTypeTitle(modelElementReference.getReferenceType().getFaName());

        modelElementReferenceDto.setElementId(modelElementReference.getElementId());
        return modelElementReferenceDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelElementRefrence create(ModelElementReferenceDto modelElementReferenceDto) {
        Assert.notNull(modelElementReferenceDto, "modelElementReferenceDto is null");

        modelVersionService.checkModelVersionStatus(modelElementReferenceDto.getModelVersionId());

        if (modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.EXTERNAL.getCode()) ||
                modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())
        ) {
            return createOrUpdateModelElementReference(modelElementReferenceDto);
        } else {
            return createModelElementReference(modelElementReferenceDto);
        }

    }

    @Override
    public Optional<ModelElementRefrence> findByElementIdAndModelVersionIdAndModelRefrenceIdAndReferenceType(String elementId, Long modelVersionId, Long referenceId, String referenceType) {
        return this.getRepository().findByElementIdAndModelVersionIdAndModelRefrenceIdAndReferenceType(elementId, modelVersionId, referenceId, referenceType, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    private ModelElementRefrence createOrUpdateModelElementReference(ModelElementReferenceDto modelElementReferenceDto) {
        Assert.notNull(modelElementReferenceDto, "modelElementReferenceDto is null");


        List<ModelElementRefrence> findModelElementRefrence = getRepository().findByElementIdAndModelVersionIdAndReferenceTypeList(modelElementReferenceDto.getElementId()
                , modelElementReferenceDto.getModelVersionId(), SecurityUtils.getCurrentOrganIdOrElseThrow(), Arrays.asList(ModelElementRefrence.ReferenceType.INTERNAL.getCode(),
                        ModelElementRefrence.ReferenceType.EXTERNAL.getCode()));
        if (findModelElementRefrence.isEmpty()) {
            return createModelElementReference(modelElementReferenceDto);
        } else {
            findModelElementRefrence.get(0).setReferenceId(modelElementReferenceDto.getModelReferenceId());
            return save(findModelElementRefrence.get(0));
        }
    }

    private ModelElementRefrence createModelElementReference(ModelElementReferenceDto modelElementReferenceDto) {
        ModelElementRefrence modelElementRefrence = new ModelElementRefrence();
        modelElementRefrence.setReferenceId(modelElementReferenceDto.getModelReferenceId());
        modelElementRefrence.setElementId(modelElementReferenceDto.getElementId());
        modelElementRefrence.setModelVersion(modelVersionService.getById(modelElementReferenceDto.getModelVersionId()));
        modelElementRefrence.setReferenceType(codeCategoryService.findByCategoryAndCode(ModelElementRefrence.ReferenceType.CATEGORY, modelElementReferenceDto.getReferenceTypeCode()));
        return save(modelElementRefrence);
    }


    @Override
    public void deleteAllReferenceByElementIdAndModelVersion(Long modelVersionId, String elementId) {
        modelVersionService.checkModelVersionStatus(modelVersionId);
        List<ModelElementRefrence> modelElementRefrences = getRepository().findByElementIdAndModelVersionIdAndReferenceTypeList(elementId, modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(),
                Arrays.stream(ModelElementRefrence.ReferenceType.values()).map(ModelElementRefrence.ReferenceType::getCode).toList());
        try {
            this.deleteAll(modelElementRefrences);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModelElementRefrence edit(ModelElementReferenceDto modelElementReferenceDto) {

        ModelElementRefrence findModelElementRefrence = findByIdOrElseThrow(modelElementReferenceDto.getId());
        modelVersionService.checkModelVersionStatus(findModelElementRefrence.getModelVersion().getId());

        findModelElementRefrence.setReferenceId(modelElementReferenceDto.getModelReferenceId());
        findModelElementRefrence.setElementId(modelElementReferenceDto.getElementId());
        findModelElementRefrence.setModelVersion(modelVersionService.getById(modelElementReferenceDto.getModelVersionId()));
        findModelElementRefrence.setReferenceType(codeCategoryService.findByCategoryAndCode(ModelElementRefrence.ReferenceType.CATEGORY, modelElementReferenceDto.getReferenceTypeCode()));
        findModelElementRefrence.setElementId(modelElementReferenceDto.getElementId());
        return save(findModelElementRefrence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelElementReferenceDto> list(ModelElementReferenceDto modelElementReferenceDto) {
        return getRepository().findAllByModelVersionIdAndOrganId(modelElementReferenceDto.getModelVersionId(),
                        SecurityUtils.getCurrentOrganIdOrElseThrow(), modelElementReferenceDto.getModelReferenceTypeList()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ModelElementRefrence> findByModelReferenceIdAndReferenceType(Long modelRefrenceId, String referenceType) {
        if (referenceType.equals(ModelElementRefrence.ReferenceType.EXTERNAL.getCode())) {
            return getRepository().findExternalProcessRefByReferenceId(modelRefrenceId, SecurityUtils.getCurrentOrganIdOrElseThrow(), referenceType);
        } else if (referenceType.equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())) {
            return getRepository().findModelVersionRefByReferenceId(modelRefrenceId, SecurityUtils.getCurrentOrganIdOrElseThrow(), referenceType);
        } else {
            //Comment ,post,job,user,unit is list
            return Optional.empty();
        }

    }

    @Override
    public SearchDto getInternalProcessList(Long modelVersionId, ModelElementReferenceDto modelElementReferenceDto, String modelName, int page, int size) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        IoSupCustDto ioSupCustDto = new IoSupCustDto();
        ioSupCustDto.setModelTypeCodeList(modelElementReferenceDto.getModelTypeCodeList());
        ioSupCustDto.setFilterHasReference(modelElementReferenceDto.getFilterHasReference());
        return ioSupCustService.getInternalProcessList(modelVersion.getModel().getId(), ioSupCustDto,
                Collections.singletonList(modelVersion.getAsisTobe().getCode()), modelName, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelElementRefrence> findLockedByModelVersionId(Long modelVersionId) {
        return getRepository().findLockedByModelVersionId(modelVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelElementRefrence> findLastByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId) {
        return getRepository().findLastByReferenceTypeCodeAndReferenceId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                referenceTypeCode, referenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelElementRefrence> findLastLockedByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId) {
        return getRepository().findLastLockedByReferenceTypeCodeAndReferenceId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                referenceTypeCode, referenceId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateInternalReferenceId(Long newReferenceId, Long oldReferenceId) {
        Assert.notNull(newReferenceId, "newReferenceId is null");
        Assert.notNull(oldReferenceId, "oldReferenceId is null");
        getRepository().updateInternalReferenceId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                ModelElementRefrence.ReferenceType.INTERNAL.getCode(),
                newReferenceId,
                oldReferenceId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateInternalReferenceIdWithLock(Long newReferenceId, Long oldReferenceId) {
        Assert.notNull(newReferenceId, "newReferenceId is null");
        Assert.notNull(oldReferenceId, "oldReferenceId is null");
        findLastLockedByReferenceTypeCodeAndReferenceId(ModelElementRefrence.ReferenceType.INTERNAL.getCode(), oldReferenceId);
        updateInternalReferenceId(newReferenceId, oldReferenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelElementRefrence clone(ModelElementRefrence modelElementRefrence) {
        Asserts.notNull(modelElementRefrence, "modelElementRefrence is null");
        modelElementRefrence = findByIdOrElseThrow(modelElementRefrence.getId());
        ModelElementRefrence clone = new ModelElementRefrence();
        clone.setModelVersion(modelElementRefrence.getModelVersion());
        clone.setReferenceId(modelElementRefrence.getReferenceId());
        clone.setElementId(modelElementRefrence.getElementId());
        clone.setReferenceType(modelElementRefrence.getReferenceType());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public void copyModelElementRefrenceIfExist(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<ModelElementRefrence> modelElementRefrenceList = findLockedByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(modelElementRefrenceList)) {
            return;
        }
        final ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        modelElementRefrenceList.forEach(modelElementRefrence -> {
            ModelVersion lastVersion = null;
            if (modelElementRefrence.getReferenceType().getCode().equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())) {
                ModelVersion reference = modelVersionService.findByIdOrElseThrow(modelElementRefrence.getReferenceId());
                lastVersion = modelVersionService.getLastVersion(modelVersionTo.getAsisTobe().getCode(), reference.getModel().getId());
                if (lastVersion == null) {
                    return;
                }
            }
            ModelElementRefrence cloned = clone(modelElementRefrence);
            cloned.setModelVersion(modelVersionTo);
            if (lastVersion != null) {
                cloned.setReferenceId(lastVersion.getId());
            }
            save(cloned);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByReferencedModelId(Long referencedModelId) {
        return getRepository().countByReferencedModelId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                ModelElementRefrence.ReferenceType.INTERNAL.getCode(), referencedModelId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId) {
        return getRepository().countByReferenceTypeCodeAndReferenceId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                referenceTypeCode, referenceId);
    }




}
