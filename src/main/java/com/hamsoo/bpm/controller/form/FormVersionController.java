package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.form.FormVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.service.api.form.FormVersionService;
import com.hamsoo.bpm.validator.form.FormVersionValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FormVersionController {

    private final FormVersionService formVersionService;
    private final FormVersionValidator formVersionValidator;

    public FormVersionController(FormVersionService formVersionService,
                                 FormVersionValidator formVersionValidator) {
        this.formVersionService = formVersionService;
        this.formVersionValidator = formVersionValidator;
    }

    @InitBinder("formVersionDto")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(formVersionValidator);
    }

    @GetMapping("/form-versions/{id}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findFormVersionById(@PathVariable("id") Long id) {
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(id);
        FormVersionDto formVersionDto = formVersionService.mapToDto(formVersion);
        return new ResponseEntity<>(formVersionDto, HttpStatus.OK);
    }

    @PostMapping("/form-versions")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> createNewVersion(@Valid @RequestBody FormVersionDto formVersionDto) {
        FormVersion formVersion = formVersionService.createNewVersion(formVersionDto);
        formVersionDto = formVersionService.mapToDto(formVersion);
        return new ResponseEntity<>(formVersionDto, HttpStatus.OK);
    }

    @PutMapping("/form-versions")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editFormVersion(@Valid @RequestBody FormVersionDto formVersionDto) {
        FormVersion formVersion = formVersionService.edit(formVersionDto);
        formVersionDto = formVersionService.mapToDto(formVersion);
        return new ResponseEntity<>(formVersionDto, HttpStatus.OK);
    }

    @PostMapping(value = "/form-versions/list/{formCatalogId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFormVersionList(@PathVariable Long formCatalogId,
                                                @RequestBody FormVersionDto formVersionDto,
                                                @RequestParam int page, @RequestParam int size,
                                                @RequestParam(required = false) String orderBy,
                                                @RequestParam(required = false) Sort.Direction direction) {
        formVersionDto.setFormCatalogId(formCatalogId);
        SearchDto searchDto = formVersionService.list(formVersionDto, page, size, orderBy, direction,
                formVersionService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
