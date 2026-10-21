package com.hamsoo.bpm.controller.resourcetype;

import com.hamsoo.bpm.model.dto.resourcetype.ResourceTypeDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.service.api.resourcetype.ResourceTypeService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize(value = "hasAuthority(@resourceItemService.RESOURCE_TYPE_MNG)")
public class ResourceTypeController {

    private final ResourceTypeService resourceTypeService;

    public ResourceTypeController(ResourceTypeService resourceTypeService) {
        this.resourceTypeService = resourceTypeService;
    }

    @GetMapping("/resource-types/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        ResourceType resourceType = resourceTypeService.findByIdOrElseThrow(id);
        ResourceTypeDto resourceTypeDto = resourceTypeService.mapToDto(resourceType);
        return new ResponseEntity<>(resourceTypeDto, HttpStatus.OK);
    }

    @GetMapping("/resource-types/list")
    public ResponseEntity<?> getList() {
        List<ResourceTypeDto> resourceTypeDtoList = resourceTypeService.getList();
        return new ResponseEntity<>(resourceTypeDtoList, HttpStatus.OK);
    }

    @PostMapping(value = "/resource-types")
    public ResponseEntity<?> saveResourceType(@Valid @RequestBody ResourceTypeDto resourceTypeDto) {
        ResourceType resourceType = resourceTypeService.create(resourceTypeDto);
        resourceTypeDto = resourceTypeService.mapToDto(resourceType);
        return new ResponseEntity<>(resourceTypeDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/resource-types")
    public ResponseEntity<?> editResourceType(@Valid @RequestBody ResourceTypeDto resourceTypeDto) {
        ResourceType resourceType = resourceTypeService.edit(resourceTypeDto);
        resourceTypeDto = resourceTypeService.mapToDto(resourceType);
        return new ResponseEntity<>(resourceTypeDto, HttpStatus.OK);
    }

    @PostMapping(value = "/resource-types/list")
    public ResponseEntity<?> getResourceTypeList(@RequestBody ResourceTypeDto resourceTypeDto, @RequestParam int page, @RequestParam int size,
                                                    @RequestParam(required = false) String orderBy,
                                                    @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = resourceTypeService.list(resourceTypeDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @DeleteMapping("/resource-types/{id}")
    public ResponseEntity<?> passiveResourceType(@PathVariable("id") Long id) {
        resourceTypeService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}