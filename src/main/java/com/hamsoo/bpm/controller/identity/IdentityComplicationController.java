package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityComplicationDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityComplication;
import com.hamsoo.bpm.service.api.identity.IdentityComplicationService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class IdentityComplicationController {

    private final IdentityComplicationService IdentityComplicationService;

    public IdentityComplicationController(IdentityComplicationService IdentityComplicationService) {
        this.IdentityComplicationService = IdentityComplicationService;
    }

    @GetMapping("/identity-complication/{id}")
    @PreAuthorize("hasAnyResourceByIdentityComplicationId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> findIdentityComplicationById(@PathVariable("id") Long id) {
        IdentityComplication IdentityComplication = IdentityComplicationService.findByIdOrElseThrow(id);
        IdentityComplicationDto identityIoDto = IdentityComplicationService.mapToDto(IdentityComplication);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }


    @PostMapping("/identity-complication")
    @PreAuthorize("hasAnyResourceByModelVersionId(#IdentityComplicationDto.modelVersionId,@resourceItemService.MODEL_Complication_ADD)")
    public ResponseEntity<?> createIdentityComplication(@Valid @RequestBody IdentityComplicationDto IdentityComplicationDto) {
        IdentityComplication IdentityComplication = IdentityComplicationService.create(IdentityComplicationDto);
        IdentityComplicationDto = IdentityComplicationService.mapToDto(IdentityComplication);
        return new ResponseEntity<>(IdentityComplicationDto, HttpStatus.OK);
    }


    @DeleteMapping("/identity-complication/{id}")
    @PreAuthorize("hasAnyResourceByIdentityComplicationId(#id,@resourceItemService.MODEL_Complication_DELETE)")
    public ResponseEntity<?> passiveIdentityComplication(@PathVariable("id") Long id) {
        IdentityComplicationService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-complication/list/{modelVersionId}")
    public ResponseEntity<?> IdentityComplicationList(@PathVariable Long modelVersionId, @RequestBody IdentityComplicationDto IdentityComplicationDto, @RequestParam int page, @RequestParam int size, @RequestParam(required = false) String orderBy, @RequestParam(required = false) Sort.Direction direction) {
        IdentityComplicationDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = IdentityComplicationService.list(IdentityComplicationDto, page, size, orderBy, direction, IdentityComplicationService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }


    @PutMapping(value = "/identity-complication/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_Complication_EDIT)")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody IdentityComplicationDto identityComplicationDto) {
        IdentityComplication identityComplication = IdentityComplicationService.edit(id, identityComplicationDto);
        identityComplicationDto = IdentityComplicationService.mapToDto(identityComplication);
        return new ResponseEntity<>(identityComplicationDto, HttpStatus.OK);
    }


}
