package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityResourceDto;
import com.hamsoo.bpm.model.entity.identity.IdentityResource;
import com.hamsoo.bpm.service.api.identity.IdentityResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class IdentityResourceController {

    private final IdentityResourceService identityResourceService;

    public IdentityResourceController(IdentityResourceService identityResourceService) {
        this.identityResourceService = identityResourceService;
    }

    @GetMapping("/identity-resource/{id}")
    @PreAuthorize("hasAnyResourceByIdentityResourceId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> findIdentityResourceById(@PathVariable("id") Long id) {
        IdentityResource IdentityResource = identityResourceService.findByIdOrElseThrow(id);
        IdentityResourceDto identityIoDto = identityResourceService.mapToDto(IdentityResource);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }


    @PostMapping("/identity-resource")
    @PreAuthorize("hasAnyResourceByModelVersionId(#IdentityResourceDto.modelVersionId,@resourceItemService.MODEL_RESOURCE_EDIT)")
    public ResponseEntity<?> createIdentityResource(@Valid @RequestBody IdentityResourceDto IdentityResourceDto) {
        IdentityResource IdentityResource = identityResourceService.create(IdentityResourceDto);
        IdentityResourceDto = identityResourceService.mapToDto(IdentityResource);
        return new ResponseEntity<>(IdentityResourceDto, HttpStatus.OK);
    }


    @DeleteMapping("/identity-resource/{id}")
    @PreAuthorize("hasAnyResourceByIdentityResourceId(#id,@resourceItemService.MODEL_RESOURCE_EDIT)")
    public ResponseEntity<?> passiveIdentityResource(@PathVariable("id") Long id) {
        identityResourceService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-resource/list/{modelVersionId}")
    public ResponseEntity<?> IdentityResourceList(@PathVariable Long modelVersionId, @RequestBody IdentityResourceDto IdentityResourceDto) {
        IdentityResourceDto.setModelVersionId(modelVersionId);
        Map<String, List<IdentityResourceDto>> searchDto = identityResourceService.list(IdentityResourceDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }


    @PutMapping(value = "/identity-resource/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_RESOURCE_EDIT)")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody IdentityResourceDto IdentityResourceDto) {
        IdentityResource IdentityResource = identityResourceService.edit(id, IdentityResourceDto);
        IdentityResourceDto = identityResourceService.mapToDto(IdentityResource);
        return new ResponseEntity<>(IdentityResourceDto, HttpStatus.OK);
    }

//    @InitBinder("resourceDto")
//    private void initResourceSchemaBinder(WebDataBinder binder) {
//        binder.setValidator(resourceSchemaValidator);
//    }


    //****  last version with jsonField in modelversion table ****//
//    @PostMapping("/identities/{modelVersionId}/resources")
//    @PreAuthorize(hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_RESOURCE_EDIT)")
//    public ResponseEntity<?> addResourceSchema(@PathVariable("modelVersionId") Long modelVersionId,
//                                               @Valid @RequestBody ResourceDto resourceDto) {
//        resourceDto = identityResourceService.addResourceSchema(modelVersionId, resourceDto);
//        return new ResponseEntity<>(resourceDto, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/identities/{modelVersionId}/resources/{resourceSchemaId}")
//    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_RESOURCE_EDIT)")
//    public ResponseEntity<?> deleteResourceSchema(@PathVariable("modelVersionId") Long modelVersionId,
//                                                  @PathVariable("resourceSchemaId") String resourceSchemaId) {
//        identityResourceService.deleteResourceSchema(modelVersionId, resourceSchemaId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/identities/{modelVersionId}/resources/list")
//    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
//    public ResponseEntity<?> getResourceSchemaList(@PathVariable("modelVersionId") Long modelVersionId) {
//        List<ResourceDto> resourceDtoList = identityResourceService.getResourceSchemaList(modelVersionId);
//        return new ResponseEntity<>(resourceDtoList, HttpStatus.OK);
//    }
//
//    @GetMapping("/identities/{modelVersionId}/resources/datailslist")
//    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
//    public ResponseEntity<?> getResourceDetailsList(@PathVariable("modelVersionId") Long modelVersionId) {
//        List<ResourceDto> resourceDtoList = identityResourceService.getResourceDetailsList(modelVersionId);
//        return new ResponseEntity<>(resourceDtoList, HttpStatus.OK);
//    }

}
