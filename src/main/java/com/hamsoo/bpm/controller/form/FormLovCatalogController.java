package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.dto.form.FormLovCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.form.FormLovCatalog;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.form.FormLovCatalogService;
import com.hamsoo.bpm.validator.form.FormLovCatalogValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FormLovCatalogController {

    private final FormLovCatalogService formLovCatalogService;
    private final CodeCategoryService codeCategoryService;
    private final FormLovCatalogValidator formLovCatalogValidator;

    public FormLovCatalogController(FormLovCatalogService formLovCatalogService,
                                    CodeCategoryService codeCategoryService,
                                    FormLovCatalogValidator formLovCatalogValidator) {
        this.formLovCatalogService = formLovCatalogService;
        this.codeCategoryService = codeCategoryService;
        this.formLovCatalogValidator = formLovCatalogValidator;
    }

    @InitBinder("formLovCatalogDto")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(formLovCatalogValidator);
    }

    @GetMapping("/form-lov-catalogs/{id}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findFormLovCatalogById(@PathVariable("id") Long id) {
        FormLovCatalog formLovCatalog = formLovCatalogService.findByIdOrElseThrow(id);
        FormLovCatalogDto formLovCatalogDto = formLovCatalogService.mapToDto(formLovCatalog);
        return new ResponseEntity<>(formLovCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/form-lov-catalogs")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> createFormLovCatalog(@Valid @RequestBody FormLovCatalogDto formLovCatalogDto) {
        FormLovCatalog formLovCatalog = formLovCatalogService.create(formLovCatalogDto);
        formLovCatalogDto = formLovCatalogService.mapToDto(formLovCatalog);
        return new ResponseEntity<>(formLovCatalogDto, HttpStatus.OK);
    }

    @PutMapping("/form-lov-catalogs")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editFormLovCatalog(@Valid @RequestBody FormLovCatalogDto formLovCatalogDto) {
        FormLovCatalog formLovCatalog = formLovCatalogService.edit(formLovCatalogDto);
        formLovCatalogDto = formLovCatalogService.mapToDto(formLovCatalog);
        return new ResponseEntity<>(formLovCatalogDto, HttpStatus.OK);
    }

    @DeleteMapping("/form-lov-catalogs/{id}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> passiveFormLovCatalog(@PathVariable Long id) {
        formLovCatalogService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/form-lov-catalogs/list")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFormLovCatalogList(@RequestBody FormLovCatalogDto formLovCatalogDto,
                                  @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = formLovCatalogService.list(formLovCatalogDto, page, size, orderBy, direction,
                formLovCatalogService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping("/form-lov-catalogs/type-list")
    //    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFormLovCatalogTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(FormLovCatalog.Type.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }

}
