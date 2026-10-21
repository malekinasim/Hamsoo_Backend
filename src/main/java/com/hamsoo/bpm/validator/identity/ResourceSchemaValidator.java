package com.hamsoo.bpm.validator.identity;

import com.hamsoo.bpm.model.dto.identity.ResourceDto;
import com.hamsoo.bpm.model.dto.identity.ResourceSchema;
import com.hamsoo.bpm.service.api.resourcecatalog.ResourceCatalogService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class ResourceSchemaValidator extends HamsooValidator {

    private final ResourceCatalogService resourceCatalogService;

    public ResourceSchemaValidator(ResourceCatalogService resourceCatalogService) {
        this.resourceCatalogService = resourceCatalogService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ResourceDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        ResourceDto dto = (ResourceDto) target;
        //if mandatory property is null not continue.
        if (errors.hasErrors()) {
            return;
        }

        if (dto.getResourceType().equals(ResourceSchema.ResourceType.RESOURCE_CATALOG)) {
            //validate resourceCatalogId is valid
            resourceCatalogService.findByIdOrElseThrow(dto.getResourceCatalogId());
        }
        //todo  after implement required services
    }
}
