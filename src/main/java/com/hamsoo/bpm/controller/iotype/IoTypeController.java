package com.hamsoo.bpm.controller.iotype;

import com.hamsoo.bpm.model.dto.iotype.IoTypeDto;
import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.service.api.iotype.IoTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyAuthority(@resourceItemService.IO_TYPE_MNG)")
public class IoTypeController {

    private final IoTypeService ioTypeService;

    public IoTypeController(IoTypeService ioTypeService) {
        this.ioTypeService = ioTypeService;
    }

    @GetMapping("/io-types/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        IoType ioType = ioTypeService.findByIdOrElseThrow(id);
        IoTypeDto ioTypeDto = ioTypeService.mapToDto(ioType);
        return new ResponseEntity<>(ioTypeDto, HttpStatus.OK);
    }

    @GetMapping("/io-types/list")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> getList() {
        List<IoTypeDto> ioTypeDtoList = ioTypeService.getList();
        return new ResponseEntity<>(ioTypeDtoList, HttpStatus.OK);
    }
}