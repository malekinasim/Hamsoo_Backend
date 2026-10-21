package com.hamsoo.bpm.controller.iocatalog;

import com.hamsoo.bpm.model.dto.iocatalog.IoCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.service.api.iocatalog.IoCatalogService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class IoCatalogController {

    private final IoCatalogService ioCatalogService;

    public IoCatalogController(IoCatalogService ioCatalogService) {
        this.ioCatalogService = ioCatalogService;
    }

    @GetMapping("/io-catalogs/{id}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> findIoCatalogById(@PathVariable("id") Long id) {
        IoCatalog ioCatalog = ioCatalogService.findByIdOrElseThrow(id);
        IoCatalogDto ioCatalogDto = ioCatalogService.mapToDto(ioCatalog);
        return new ResponseEntity<>(ioCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/io-catalogs")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> createIoCatalog(@Valid @RequestBody IoCatalogDto ioCatalogDto) {
        IoCatalog ioCatalog = ioCatalogService.create(ioCatalogDto);
        ioCatalogDto = ioCatalogService.mapToDto(ioCatalog);
        return new ResponseEntity<>(ioCatalogDto, HttpStatus.OK);
    }

    @PutMapping("/io-catalogs")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> editIoCatalog(@Valid @RequestBody IoCatalogDto ioCatalogDto) {
        IoCatalog ioCatalog = ioCatalogService.edit(ioCatalogDto);
        ioCatalogDto = ioCatalogService.mapToDto(ioCatalog);
        return new ResponseEntity<>(ioCatalogDto, HttpStatus.OK);
    }

    @DeleteMapping("/io-catalogs/{id}")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> passiveIoCatalog(@PathVariable Long id) {
        ioCatalogService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/io-catalogs/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.INPUT_OUTPUT_MNG)")
    public ResponseEntity<?> getIoCatalogList(@RequestBody IoCatalogDto ioCatalogDto,
                                              @RequestParam int page, @RequestParam int size,
                                              @RequestParam(required = false) String orderBy,
                                              @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = ioCatalogService.list(ioCatalogDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
