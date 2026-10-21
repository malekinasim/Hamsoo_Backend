package com.hamsoo.bpm.validator.identity;

import com.hamsoo.bpm.model.dto.identity.IoSupCustDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

@Component
public class IoSupCustValidator extends HamsooValidator {

    private final CodeCategoryService codeCategoryService;
    private final ModelVersionService modelVersionService;
    private final ExternalProcessService externalProcessService;
    private final IdentityIoService identityIoService;

    public IoSupCustValidator(CodeCategoryService codeCategoryService,
                              ModelVersionService modelVersionService,
                              ExternalProcessService externalProcessService,
                              IdentityIoService identityIoService) {
        this.codeCategoryService = codeCategoryService;
        this.modelVersionService = modelVersionService;
        this.externalProcessService = externalProcessService;
        this.identityIoService = identityIoService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return IoSupCustDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        IoSupCustDto dto = (IoSupCustDto) target;

        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        CodeCategory type = codeCategoryService.findByCategoryAndCode(IoSupCust.Type.CATEGORY, dto.getTypeCode());
        if (type == null) {
            errors.rejectValue("typeCode", "", "error.ioSupCust.typeCodeIsNotValid");
            return;
        }

        if (IoSupCust.Type.INTERNAL_PROCESS.getCode().equals(type.getCode())) {
            if (dto.getInternalSupCustId() == null) {
                errors.rejectValue("internalSupCustId", "", "error.ioSupCust.internalSupCustIdIsNull");
            } else {
                ModelVersion internalSupCust = modelVersionService.findByIdOrElseThrow(dto.getInternalSupCustId());
                IdentityIo identityIo = identityIoService.findByIdOrElseThrow(dto.getIdentityIoId());
                if (identityIo.getModelVersion().getModel().getId().equals(internalSupCust.getModel().getId())) {
                    errors.rejectValue("internalSupCustId", "", "error.ioSupCust.internalSupCustCannotRecursive");
                }
            }

        } else if (IoSupCust.Type.EXTERNAL_PROCESS.getCode().equals(type.getCode())) {
            if (dto.getExternalSupCustId() == null) {
                errors.rejectValue("externalSupCustId", "", "error.ioSupCust.externalSupCustIdIsNull");
            } else {
                externalProcessService.findByIdOrElseThrow(dto.getExternalSupCustId());
            }

        } else if (IoSupCust.Type.EVENT.getCode().equals(type.getCode())) {
            IdentityIo identityIo = identityIoService.findByIdOrElseThrow(dto.getIdentityIoId());
            if (!identityIo.getType().getCode().equals(IdentityIo.Type.INPUT.getCode())) {
                errors.rejectValue("eventSupCust", "", "error.ioSupCust.eventJustCanAppliedToInput");
                return;
            }
            if (!StringUtils.hasText(dto.getEventSupCust())) {
                errors.rejectValue("eventSupCust", "", "error.ioSupCust.eventSupCustIsNull");
            }
        }

    }


}
