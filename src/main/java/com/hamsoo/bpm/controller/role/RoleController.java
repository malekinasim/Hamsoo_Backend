package com.hamsoo.bpm.controller.role;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.service.api.role.RoleService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RoleController {


    private final RoleService roleService;


    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @PostMapping(value = "/role/list")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGAN_MEMBER_MNG,@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> list(@RequestBody RoleDto roleDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = roleService.list(roleDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

    @PostMapping(value = "/role/page")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGAN_MEMBER_MNG,@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> list(@RequestBody RoleDto roleDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = roleService.list(roleDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}