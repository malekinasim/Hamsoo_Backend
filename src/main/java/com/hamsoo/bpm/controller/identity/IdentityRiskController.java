package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityRiskDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityRisk;
import com.hamsoo.bpm.service.api.identity.IdentityRiskService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class IdentityRiskController {

    private final IdentityRiskService IdentityRiskService;

    public IdentityRiskController(com.hamsoo.bpm.service.api.identity.IdentityRiskService identityRiskService) {
        IdentityRiskService = identityRiskService;
    }

    @GetMapping("/identity-risk/{id}")
    @PreAuthorize("hasAnyResourceByIdentityRiskId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> findIdentityRiskById(@PathVariable("id") Long id) {
        IdentityRisk IdentityRisk = IdentityRiskService.findByIdOrElseThrow(id);
        IdentityRiskDto identityIoDto = IdentityRiskService.mapToDto(IdentityRisk);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }

    @PostMapping("/identity-risk")
    @PreAuthorize("hasAnyResourceByModelVersionId(#IdentityRiskDto.modelVersionId,@resourceItemService.MODEL_Risk_ADD)")
    public ResponseEntity<?> createIdentityRisk(@Valid @RequestBody IdentityRiskDto IdentityRiskDto) {
        IdentityRisk IdentityRisk = IdentityRiskService.create(IdentityRiskDto);
        IdentityRiskDto = IdentityRiskService.mapToDto(IdentityRisk);
        return new ResponseEntity<>(IdentityRiskDto, HttpStatus.OK);
    }


    @DeleteMapping("/identity-risk/{id}")
    @PreAuthorize("hasAnyResourceByIdentityRiskId(#id,@resourceItemService.MODEL_Risk_DELETE)")
    public ResponseEntity<?> passiveIdentityRisk(@PathVariable("id") Long id) {
        IdentityRiskService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-risk/list/{modelVersionId}")
    public ResponseEntity<?> IdentityRiskList(@PathVariable Long modelVersionId,
                                             @RequestBody IdentityRiskDto IdentityRiskDto,
                                             @RequestParam int page, @RequestParam int size,
                                             @RequestParam(required = false) String orderBy,
                                             @RequestParam(required = false) Sort.Direction direction) {
        IdentityRiskDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = IdentityRiskService.list(IdentityRiskDto, page, size, orderBy, direction,
                IdentityRiskService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PutMapping(value = "/identity-risk/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_Risk_EDIT)")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody IdentityRiskDto IdentityRiskDto) {
        IdentityRisk identityRisk = IdentityRiskService.edit(id, IdentityRiskDto);
        IdentityRiskDto = IdentityRiskService.mapToDto(identityRisk);
        return new ResponseEntity<>(IdentityRiskDto, HttpStatus.OK);
    }

}
