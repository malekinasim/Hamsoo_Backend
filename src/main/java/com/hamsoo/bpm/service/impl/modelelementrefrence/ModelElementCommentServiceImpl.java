package com.hamsoo.bpm.service.impl.modelelementrefrence;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementCommentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ModelElementCommentServiceImpl implements ModelElementCommentService {

    private final ModelCommentService modelCommentService;
    private final ModelVersionService modelVersionService;
    private final ModelElementReferenceService modelElementReferenceService;

    public ModelElementCommentServiceImpl(ModelCommentService modelCommentService,
                                          ModelVersionService modelVersionService, ModelElementReferenceService modelElementReferenceService) {
        this.modelCommentService = modelCommentService;
        this.modelVersionService = modelVersionService;
        this.modelElementReferenceService = modelElementReferenceService;
    }

    /**
     * if elementId exist, a ModelElementReference is created too.
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelCommentDto create(ModelCommentDto modelCommentDto) {
        Assert.notNull(modelCommentDto, "modelCommentDto is null");
        ModelComment modelComment = modelCommentService.create(modelCommentDto);
        String elementId = modelCommentDto.getElementId();
        ModelElementRefrence modelElementRefrence = null;
        if (StringUtils.hasText(elementId)) {
            ModelElementReferenceDto modelElementReferenceDto = new ModelElementReferenceDto();
            modelElementReferenceDto.setModelReferenceId(modelComment.getId());
            modelElementReferenceDto.setElementId(elementId);
            modelElementReferenceDto.setModelVersionId(modelCommentDto.getModelVersionId());
            modelElementReferenceDto.setReferenceTypeCode(ModelElementRefrence.ReferenceType.COMMENT.getCode());
            modelElementRefrence = modelElementReferenceService.create(modelElementReferenceDto);
        }
        return mapToDto(modelComment, modelElementRefrence);
    }

    private ModelCommentDto mapToDto(ModelComment modelComment, ModelElementRefrence modelElementRefrence) {
        ModelCommentDto modelCommentDto = modelCommentService.mapToDto(modelComment);
        if (modelElementRefrence != null) {
            modelCommentDto.setModelElementReferenceId(modelElementRefrence.getId());
            modelCommentDto.setElementId(modelElementRefrence.getElementId());
        }
        return modelCommentDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        ModelElementRefrence modelElementRefrence = modelElementReferenceService.findByIdOrElseThrow(id);
        if (!modelElementRefrence.getReferenceType().getCode().equals(ModelElementRefrence.ReferenceType.COMMENT.getCode())) {
            throw new BusinessException("error.modelElementComment.idNotValid");
        }
        modelElementReferenceService.delete(id);
        Long commentId = modelElementRefrence.getReferenceId();
        List<ModelElementRefrence> modelElementCommentList =
                modelElementReferenceService.findLastByReferenceTypeCodeAndReferenceId(ModelElementRefrence.ReferenceType.COMMENT.getCode(), commentId);
        if (CollectionUtils.isEmpty(modelElementCommentList)) {
            modelCommentService.passive(commentId);
        }
    }

    @Override
    public SearchDto getElementCommentListOfLastVersionWithPageData(Long modelVersionId, String elementId, Pageable pageable) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelComment.modelVersionIdIsNotLastVersion");
        }
        Page<ModelCommentDto> ModelCommentDtoPage = modelCommentService.getElementCommentListOfLastVersion(modelVersionId, elementId, pageable);
        return new SearchDto.Builder().content(ModelCommentDtoPage.getContent())
                .totalElements(ModelCommentDtoPage.getTotalElements())
                .totalPages(ModelCommentDtoPage.getTotalPages())
                .currentPage(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    @Override
    public void deleteAllModelElementCommentByModelVersionIdAndElementId(Long modelVersionId, String elementId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelComment.modelVersionIdIsNotLastVersion");
        }
        modelVersionService.checkModelVersionStatus(modelVersionId);
        List<ModelCommentDto> ModelCommentDtoPage = modelCommentService.getAllElementCommentListOfLastVersion(modelVersionId, elementId);
        ModelCommentDtoPage.stream().forEach(modelCommentDto ->
                this.delete(modelCommentDto.getId())
        );
    }

}
