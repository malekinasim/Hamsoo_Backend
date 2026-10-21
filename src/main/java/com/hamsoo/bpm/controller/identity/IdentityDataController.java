package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityDataDto;
import com.hamsoo.bpm.service.api.identity.IdentityDataService;
import com.hamsoo.bpm.validator.identity.IdentityDataSchemaValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class IdentityDataController {

    private final IdentityDataService identityDataService;
    private final IdentityDataSchemaValidator identityDataSchemaValidator;

    public IdentityDataController(IdentityDataService identityDataService,
                                  IdentityDataSchemaValidator identityDataSchemaValidator) {
        this.identityDataService = identityDataService;
        this.identityDataSchemaValidator = identityDataSchemaValidator;
    }

    @InitBinder("identityDataDto")
    private void initIdentityDataSchemaBinder(WebDataBinder binder) {
        binder.setValidator(identityDataSchemaValidator);
    }

    @PostMapping("/identities/{modelVersionId}/identityData")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.PROCESS_GENERAL_MNG)")
    public ResponseEntity<?> updateIdentityDataSchema(@PathVariable("modelVersionId") Long modelVersionId,
                                                      @Valid @RequestBody IdentityDataDto identityDataDto) {
        identityDataDto = identityDataService.updateIdentityDataSchema(modelVersionId, identityDataDto);
        return new ResponseEntity<>(identityDataDto, HttpStatus.OK);
    }

    @GetMapping("/identities/{modelVersionId}/identityData")
    // @PreAuthorize(value = "isAnonymous()")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> getIdentityDataSchema(@PathVariable("modelVersionId") Long modelVersionId) {
        IdentityDataDto identityDataDto = identityDataService.getIdentityDataSchema(modelVersionId);
        return new ResponseEntity<>(identityDataDto, HttpStatus.OK);
    }
}
