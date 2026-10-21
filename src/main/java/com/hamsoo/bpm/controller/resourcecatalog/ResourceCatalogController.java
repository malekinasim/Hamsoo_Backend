package com.hamsoo.bpm.controller.resourcecatalog;

import com.hamsoo.bpm.model.dto.resourcecatalog.ResourceCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog;
import com.hamsoo.bpm.service.api.resourcecatalog.ResourceCatalogService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")

public class ResourceCatalogController {

    private final ResourceCatalogService resourceCatalogService;


    public ResourceCatalogController(ResourceCatalogService resourceCatalogService) {
        this.resourceCatalogService = resourceCatalogService;
    }

    @GetMapping("/resource-catalogs/{id}")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.RESOURCE_MNG)")
    public ResponseEntity<?> findResourceCatalogById(@PathVariable("id") Long id) {
        ResourceCatalog resourceCatalog = resourceCatalogService.findByIdOrElseThrow(id);
        ResourceCatalogDto resourceCatalogDto = resourceCatalogService.mapToDto(resourceCatalog);
        return new ResponseEntity<>(resourceCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/resource-catalogs")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.RESOURCE_MNG)")
    public ResponseEntity<?> createResourceCatalog(@Valid @RequestBody ResourceCatalogDto resourceCatalogDto) {
        ResourceCatalog resourceCatalog = resourceCatalogService.create(resourceCatalogDto);
        resourceCatalogDto = resourceCatalogService.mapToDto(resourceCatalog);
        return new ResponseEntity<>(resourceCatalogDto, HttpStatus.OK);
    }

    @PutMapping("/resource-catalogs")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.RESOURCE_MNG)")
    public ResponseEntity<?> editResourceCatalog(@Valid @RequestBody ResourceCatalogDto resourceCatalogDto) {
        ResourceCatalog resourceCatalog = resourceCatalogService.edit(resourceCatalogDto);
        resourceCatalogDto = resourceCatalogService.mapToDto(resourceCatalog);
        return new ResponseEntity<>(resourceCatalogDto, HttpStatus.OK);
    }


    @DeleteMapping("/resource-catalogs/{id}")
    @PreAuthorize(value = "hasAuthority(@resourceItemService.RESOURCE_MNG)")
    public ResponseEntity<?> passiveResourceCatalog(@PathVariable Long id) {
        resourceCatalogService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/resource-catalogs/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.RESOURCE_MNG)")
    public ResponseEntity<?> getResourceCatalogList(@RequestBody ResourceCatalogDto resourceCatalogDto, @RequestParam int page, @RequestParam int size,
                                                    @RequestParam(required = false) String orderBy,
                                                    @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = resourceCatalogService.list(resourceCatalogDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping("/resource-catalogs/list/{id}")
    public ResponseEntity<?> getList(@PathVariable Long id) {
        List<ResourceCatalogDto> resourceCatalogDtos = resourceCatalogService.getlistByResourceTypeId(id);
        return new ResponseEntity<>(resourceCatalogDtos, HttpStatus.OK);
    }

}
