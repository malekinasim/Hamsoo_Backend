package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.form.FormCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormCatalog;
import com.hamsoo.bpm.service.api.form.FormCatalogService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FormCatalogController {

    private final FormCatalogService formCatalogService;

    public FormCatalogController(FormCatalogService formCatalogService) {
        this.formCatalogService = formCatalogService;
    }

    @GetMapping("/form-catalogs/{id}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findFormCatalogById(@PathVariable("id") Long id) {
        FormCatalog formCatalog = formCatalogService.findByIdOrElseThrow(id);
        FormCatalogDto formCatalogDto = formCatalogService.mapToDto(formCatalog);
        return new ResponseEntity<>(formCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/form-catalogs")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> createFormCatalog(@Valid @RequestBody FormCatalogDto formCatalogDto) {
        FormCatalog formCatalog = formCatalogService.create(formCatalogDto);
        formCatalogDto = formCatalogService.mapToDto(formCatalog);
        return new ResponseEntity<>(formCatalogDto, HttpStatus.OK);
    }

    @PutMapping("/form-catalogs")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editFormCatalog(@Valid @RequestBody FormCatalogDto formCatalogDto) {
        FormCatalog formCatalog = formCatalogService.edit(formCatalogDto);
        formCatalogDto = formCatalogService.mapToDto(formCatalog);
        return new ResponseEntity<>(formCatalogDto, HttpStatus.OK);
    }

    @PostMapping(value = "/form-catalogs/list")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFormCatalogList(@RequestBody FormCatalogDto formCatalogDto,
                                                @RequestParam int page, @RequestParam int size,
                                                @RequestParam(required = false) String orderBy,
                                                @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = formCatalogService.list(formCatalogDto, page, size, orderBy, direction,
                formCatalogService::mapToDtoByLastVersionInfo);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
