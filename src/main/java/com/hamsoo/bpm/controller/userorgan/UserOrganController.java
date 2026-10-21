package com.hamsoo.bpm.controller.userorgan;

import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import com.hamsoo.bpm.validator.userorgan.UserOrganValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyAuthority(@resourceItemService.ORGAN_MEMBER_MNG)")
public class UserOrganController {

    private final UserOrganService userOrganService;
    private final UserOrganValidator userOrganValidator;

    public UserOrganController(UserOrganService userOrganService,
                               UserOrganValidator userOrganValidator) {
        this.userOrganService = userOrganService;
        this.userOrganValidator = userOrganValidator;

    }

    @InitBinder("userOrganDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(userOrganValidator);
    }

    @GetMapping("/user-organ/{id}")
    public ResponseEntity<?> getUserOrganById(@PathVariable("id") Long id) {
        UserOrgan userOrgan = userOrganService.findByIdOrElseThrow(id);
        UserOrganDto userOrganDto = userOrganService.mapToDto(userOrgan);
        return new ResponseEntity<>(userOrganDto, HttpStatus.OK);
    }

    @PostMapping(value = "/user-organ")

    public ResponseEntity<?> saveUserOrgan(@Valid @RequestBody UserOrganDto userOrganDto) {
        UserOrgan userOrgan = userOrganService.create(userOrganDto);
        userOrganDto = userOrganService.mapToDto(userOrgan);
        return new ResponseEntity<>(userOrganDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/user-organ")
    public ResponseEntity<?> editUserOrgan(@Valid @RequestBody UserOrganDto userOrganDto) {
        UserOrgan userOrgan = userOrganService.edit(userOrganDto);
        userOrganDto = userOrganService.mapToDto(userOrgan);
        return new ResponseEntity<>(userOrganDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/user-organ/{id}")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        userOrganService.passiveOrganMemberAndModelMemberOfOrgan(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/user-organ/list/{organId}")
    public ResponseEntity<?> list(@PathVariable Long organId,
                                  @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String filter,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = userOrganService.list(organId, page, size, filter, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping(value = "/user-organ/list/cur-organ")
    public ResponseEntity<?> listOfCurOrgan(
            @RequestParam int page, @RequestParam int size,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = userOrganService.list(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                page, size, filter, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
