package com.hamsoo.bpm.controller.externalprocess;

import com.hamsoo.bpm.model.dto.externalprocess.ExternalProcessDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ExternalProcessController {

    private final ExternalProcessService externalProcessService;

    public ExternalProcessController(ExternalProcessService externalProcessService) {
        this.externalProcessService = externalProcessService;
    }

    @GetMapping("/external-processes/{id}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.EXTERNAL_PROCESS_MNG)")
    public ResponseEntity<?> findExternalProcessById(@PathVariable("id") Long id) {
        ExternalProcess externalProcess = externalProcessService.findByIdOrElseThrow(id);
        ExternalProcessDto externalProcessDto = externalProcessService.mapToDto(externalProcess);
        return new ResponseEntity<>(externalProcessDto, HttpStatus.OK);
    }

    @PostMapping("/external-processes")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.EXTERNAL_PROCESS_MNG)")
    public ResponseEntity<?> createExternalProcess(@Valid @RequestBody ExternalProcessDto externalProcessDto) {
        ExternalProcess externalProcess = externalProcessService.create(externalProcessDto);
        externalProcessDto = externalProcessService.mapToDto(externalProcess);
        return new ResponseEntity<>(externalProcessDto, HttpStatus.OK);
    }

    @PutMapping("/external-processes")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.EXTERNAL_PROCESS_MNG)")
    public ResponseEntity<?> editExternalProcess(@Valid @RequestBody ExternalProcessDto externalProcessDto) {
        ExternalProcess externalProcess = externalProcessService.edit(externalProcessDto);
        externalProcessDto = externalProcessService.mapToDto(externalProcess);
        return new ResponseEntity<>(externalProcessDto, HttpStatus.OK);
    }

    @DeleteMapping("/external-processes/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.EXTERNAL_PROCESS_MNG)")
    public ResponseEntity<?> passiveExternalProcess(@PathVariable Long id) {
        externalProcessService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/external-processes/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.EXTERNAL_PROCESS_MNG)")
    public ResponseEntity<?> externalProcessList(@RequestBody ExternalProcessDto externalProcessDto,
                                                 @RequestParam int page, @RequestParam int size,
                                                 @RequestParam(required = false) String orderBy,
                                                 @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = externalProcessService.list(externalProcessDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
