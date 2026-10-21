package com.hamsoo.bpm.controller.resourceitem;

import com.hamsoo.bpm.model.dto.resourceitem.ResourceItemDto;
import com.hamsoo.bpm.service.api.resourceitem.ResourceItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ResourceItemController {
    private ResourceItemService resourceItemService;

    public ResourceItemController(ResourceItemService resourceItemService) {
        this.resourceItemService = resourceItemService;
    }

    @GetMapping(value = "/public/resource-items/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getResourceItemList() {
        try {
            List<ResourceItemDto> resourceItems = resourceItemService.list();
            return new ResponseEntity<>(resourceItems, HttpStatus.OK);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @GetMapping(value = "/resource-item/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getResourceItemByName(@PathVariable String name) {
        try {
            ResourceItemDto resourceItemDto = resourceItemService.getResourceItemByNamAndErrorDetails( name);
            return new ResponseEntity<>(resourceItemDto, HttpStatus.OK);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
}

