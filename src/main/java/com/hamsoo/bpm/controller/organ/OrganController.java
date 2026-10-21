package com.hamsoo.bpm.controller.organ;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.ValidationException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.validator.organ.OrganImageValidator;
import com.hamsoo.bpm.validator.organ.OrganValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrganController {

    private OrganService organService;
    private final OrganValidator organValidator;
    private final OrganImageValidator organImageValidator;

    public OrganController(OrganService organService, OrganValidator organValidator, OrganImageValidator organImageValidator) {
        this.organService = organService;
        this.organValidator = organValidator;
        this.organImageValidator = organImageValidator;
    }

    @InitBinder("organDto")
    public void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(organValidator);
    }


    //----------changeUser current Organ---------
    @GetMapping("/organ/changeUser/CurOrg/{id}")
    public ResponseEntity<?> changeUserCurrentOrgan(@PathVariable("id") Long id) {
        Organ organ = organService.findById(id).orElse(null);
        if (organ == null) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        if (SecurityUtils.getCurrentUser().isPresent() &&
                SecurityUtils.getCurrentUser().get().getOrgans().stream().anyMatch(
                        item -> item.getId().equals(id)
                )) {
            OrganDto organDto = organService.mapToDto(organ);
            return new ResponseEntity<>(organDto, HttpStatus.OK);
        }

        throw new BusinessException("error.organ.idIsNotValid");
    }

    @GetMapping("/organ/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Organ organ = organService.findByIdOrElseThrow(id);
        OrganDto organDto = organService.mapToDto(organ);
        return new ResponseEntity<>(organDto, HttpStatus.OK);
    }

    @PostMapping(value = "/organ", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority(@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> create(@Valid @RequestPart OrganDto organDto, @RequestPart(value = "image", required = false) MultipartFile image) {
        if ((image != null && validateImages(image)) || image == null) {
            Organ organ = organService.create(organDto, image);
            organDto = organService.mapToDto(organ);
            return new ResponseEntity<>(organDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/organ", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority(@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> edit(@Valid @RequestPart OrganDto organDto
            , @RequestPart(value = "image", required = false) MultipartFile image) {
        if (image != null && validateImages(image) || image == null) {
            Organ organ = organService.edit(organDto, image);
            organDto = organService.mapToDto(organ);
            return new ResponseEntity<>(organDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @DeleteMapping("/organ/{id}")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        organService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/organ/page")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> list(@RequestBody OrganDto organDto, @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = organService.list(organDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/organ/list")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.WORKSPACE_MNG)")
    public ResponseEntity<?> list(@RequestBody OrganDto organDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = organService.list(organDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

    public Boolean validateImages(MultipartFile multipartFile) {
        BindException bindException = new BindException(multipartFile, "organDto");
        organImageValidator.validate(multipartFile, bindException);
        if (bindException.hasErrors()) {
            throw new ValidationException(bindException);
        }
        return true;
    }
}
