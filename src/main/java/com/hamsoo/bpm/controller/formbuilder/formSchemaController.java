package com.hamsoo.bpm.controller.formbuilder;

import com.hamsoo.bpm.model.dto.FormBuilder.FormSchemaCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog;
import com.hamsoo.bpm.service.api.formbuilder.FormSchemaService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class formSchemaController {

    private final FormSchemaService formSchemaService;

    public formSchemaController(FormSchemaService formSchemaService) {
        this.formSchemaService = formSchemaService;
    }


    @DeleteMapping("/form-schema/{id}")
    public ResponseEntity<?> passiveFormSchema(@PathVariable("id") Long id) {
        formSchemaService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/form-schema")
    public ResponseEntity<?> createFormSchema(@Valid @RequestBody FormSchemaCatalogDto FormSchemaCatalogDto) {
        FormSchemaCatalog formSchema = formSchemaService.create(FormSchemaCatalogDto);
        FormSchemaCatalogDto = formSchemaService.mapToDto(formSchema);
        return new ResponseEntity<>(FormSchemaCatalogDto, HttpStatus.OK);
    }

    @GetMapping("/form-schema/{id}")
    public ResponseEntity<?> findFormSchemaById(@PathVariable("id") Long id) {
        FormSchemaCatalog formSchema = formSchemaService.findByIdOrElseThrow(id);
        FormSchemaCatalogDto FormSchemaCatalogDto = formSchemaService.mapToDto(formSchema);
        return new ResponseEntity<>(FormSchemaCatalogDto, HttpStatus.OK);
    }

    @PostMapping("/form-schema/edit")
    public ResponseEntity<?> editFormSchema(@Valid @RequestBody FormSchemaCatalogDto FormSchemaCatalogDto) {
        FormSchemaCatalog formSchema = formSchemaService.edit(FormSchemaCatalogDto);
        FormSchemaCatalogDto = formSchemaService.mapToDto(formSchema);
        return new ResponseEntity<>(FormSchemaCatalogDto, HttpStatus.OK);
    }

    @GetMapping(value = "/form-schema/formSchema-list/{modelId}")
    public ResponseEntity<?> list(@RequestBody FormSchemaCatalogDto FormSchemaCatalogDto,
                                          @RequestParam int page,
                                          @RequestParam int size,
                                          @RequestParam(required = false) String orderBy,
                                          @RequestParam(required = false) Sort.Direction direction) {

        SearchDto searchDto = formSchemaService.list(FormSchemaCatalogDto, page, size, orderBy, direction,formSchemaService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
