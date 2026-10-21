package com.hamsoo.bpm.validator.modelelementreference;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Optional;

@Component
public class ModelElementReferenceValidator extends HamsooValidator {
    private final ModelElementReferenceService modelElementReferenceService;
    private final ModelVersionService modelVersionService;
    private final ExternalProcessService externalProcessService;
    private final PostService postService;
    private final ModelCommentService modelCommentService;

    public ModelElementReferenceValidator(ModelElementReferenceService modelElementReferenceService, ModelVersionService modelVersionService, ExternalProcessService externalProcessService, PostService postService, ModelCommentService modelCommentService) {
        this.modelElementReferenceService = modelElementReferenceService;
        this.modelVersionService = modelVersionService;
        this.externalProcessService = externalProcessService;
        this.postService = postService;

        this.modelCommentService = modelCommentService;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ModelElementReferenceDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        ModelElementReferenceDto modelElementReferenceDto = (ModelElementReferenceDto) target;
        if (modelElementReferenceDto.getId() == null) {
            //create
            if (modelElementReferenceDto.getModelVersionId() == null) {
                errors.rejectValue("modelVersionId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.modelVersionIdIsNotValid");
            }
            if (modelElementReferenceDto.getModelReferenceId() == null) {
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.modelReferenceIdIsNotValid");
            }
            if (modelElementReferenceDto.getElementId() == null) {
                errors.rejectValue("elementId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.elementIdIsNotValid");
            }
        } else {
            //edit
            if (modelElementReferenceDto.getModelReferenceId() == null) {
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.modelReferenceIdIsNotValid");
            }
        }


        Optional<ModelElementRefrence> modelElementRefrence1 = modelElementReferenceService.findByElementIdAndModelVersionIdAndModelRefrenceIdAndReferenceType(modelElementReferenceDto.getElementId(), modelElementReferenceDto.getModelVersionId(), modelElementReferenceDto.getModelReferenceId(), modelElementReferenceDto.getReferenceTypeCode());
        if (modelElementRefrence1.isPresent())
            errors.rejectValue("elementId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.duplicateElementId.IsNotValid" +
                    "Id.IsNotValid");


        if (modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.EXTERNAL.getCode()) ||
                modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())
        ) {
            Optional<ModelElementRefrence> modelElementRefrence = modelElementReferenceService.findByModelReferenceIdAndReferenceType(modelElementReferenceDto.getModelReferenceId(), modelElementReferenceDto.getReferenceTypeCode());
            if (modelElementRefrence.isPresent())
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.duplicateReferenceId.IsNotValid");

        }
        checkModelElementType(modelElementReferenceDto, errors);

    }

    private void checkModelElementType(ModelElementReferenceDto modelElementReferenceDto, Errors errors) {
        if (modelElementReferenceDto.getReferenceTypeCode() == null ||
                Arrays.stream(ModelElementRefrence.ReferenceType.values()).noneMatch(type -> type.getCode().equals(modelElementReferenceDto.getReferenceTypeCode()))) {
            errors.rejectValue("referenceTypeCode", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error.modelElementReferenceDto.referenceTypeCodeISNotValid");
        }
        if (modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.INTERNAL.getCode())) {
            try {
                modelVersionService.findByIdOrElseThrow(modelElementReferenceDto.getModelReferenceId());
            } catch (BusinessException e) {
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
            }
        }

        if (modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.EXTERNAL.getCode())) {
            try {
                externalProcessService.findByIdOrElseThrow(modelElementReferenceDto.getModelReferenceId());
            } catch (BusinessException e) {
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
            }
        }


        if (modelElementReferenceDto.getReferenceTypeCode().equals(ModelElementRefrence.ReferenceType.COMMENT.getCode())) {
            try {
                modelCommentService.findByIdOrElseThrow(modelElementReferenceDto.getModelReferenceId());
            } catch (BusinessException e) {
                errors.rejectValue("referenceId", String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
            }
        }


    }
}
