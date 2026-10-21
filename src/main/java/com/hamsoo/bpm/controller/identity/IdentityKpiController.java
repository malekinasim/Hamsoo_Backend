package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.identity.IdentityKpiDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityKpi;
import com.hamsoo.bpm.service.api.identity.IdentityKpiService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class IdentityKpiController {

    private final IdentityKpiService identityKpiService;

    public IdentityKpiController(IdentityKpiService identityKpiService) {
        this.identityKpiService = identityKpiService;
    }

    @GetMapping("/identity-kpis/{id}")
    @PreAuthorize("hasAnyResourceByIdentityKpiId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> findIdentityKpiById(@PathVariable("id") Long id) {
        IdentityKpi identityKpi = identityKpiService.findByIdOrElseThrow(id);
        IdentityKpiDto identityIoDto = identityKpiService.mapToDto(identityKpi);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }


    @PostMapping("/identity-kpis")
    @PreAuthorize("hasAnyResourceByModelVersionId(#identityKpiDto.modelVersionId,@resourceItemService.MODEL_KPI_EDIT)")
    public ResponseEntity<?> createIdentityKpi(@Valid @RequestBody IdentityKpiDto identityKpiDto) {
        IdentityKpi identityKpi = identityKpiService.create(identityKpiDto);
        identityKpiDto = identityKpiService.mapToDto(identityKpi);
        return new ResponseEntity<>(identityKpiDto, HttpStatus.OK);
    }


    @DeleteMapping("/identity-kpis/{id}")
    @PreAuthorize("hasAnyResourceByIdentityKpiId(#id,@resourceItemService.MODEL_KPI_EDIT)")
    public ResponseEntity<?> passiveIdentityKpi(@PathVariable("id") Long id) {
        identityKpiService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-kpis/list/{modelVersionId}")
    public ResponseEntity<?> identityKpiList(@PathVariable Long modelVersionId,
                                             @RequestBody IdentityKpiDto identityKpiDto,
                                             @RequestParam int page, @RequestParam int size,
                                             @RequestParam(required = false) String orderBy,
                                             @RequestParam(required = false) Sort.Direction direction) {
        identityKpiDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = identityKpiService.list(identityKpiDto, page, size, orderBy, direction,
                identityKpiService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
