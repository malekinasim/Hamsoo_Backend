package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.form.FormEntityDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormEntity;
import com.hamsoo.bpm.service.api.form.FormEntityService;
import com.hamsoo.bpm.service.api.form.FormVersionFormEntityService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FormVersionFormEntityController {

    private final FormVersionFormEntityService formVersionFormEntityService;
    private final FormEntityService formEntityService;

    public FormVersionFormEntityController(FormVersionFormEntityService formVersionFormEntityService,
                                           FormEntityService formEntityService) {
        this.formVersionFormEntityService = formVersionFormEntityService;
        this.formEntityService = formEntityService;
    }

    @GetMapping("/form-versions/{formVersionId}/form-entities/{formEntityId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findFormEntityByIdAndFormVersionId(@PathVariable("formVersionId") Long formVersionId,
                                                                @PathVariable("formEntityId") Long formEntityId) {
        FormEntity formEntity = formVersionFormEntityService.findByIdOrElseThrow(formEntityId, formVersionId);
        FormEntityDto formEntityDto = formEntityService.mapToDto(formEntity);
        return new ResponseEntity<>(formEntityDto, HttpStatus.OK);
    }

    @PostMapping("/form-versions/{formVersionId}/form-entities")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> addFormEntity(@PathVariable Long formVersionId,
                                           @Valid @RequestBody FormEntityDto formEntityDto) {
        formEntityDto.setFormVersionId(formVersionId);
        FormEntity formEntity = formVersionFormEntityService.addFormEntity(formEntityDto);
        formEntityDto = formEntityService.mapToDto(formEntity);
        return new ResponseEntity<>(formEntityDto, HttpStatus.OK);
    }

    @PutMapping("/form-versions/{formVersionId}/form-entities")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editFormEntity(@PathVariable Long formVersionId,
                                            @Valid @RequestBody FormEntityDto formEntityDto) {
        formEntityDto.setFormVersionId(formVersionId);
        FormEntity formEntity = formVersionFormEntityService.editFormEntity(formEntityDto);
        formEntityDto = formEntityService.mapToDto(formEntity);
        return new ResponseEntity<>(formEntityDto, HttpStatus.OK);
    }

    @DeleteMapping("/form-versions/{formVersionId}/form-entities/{formEntityId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> passiveFormEntity(@PathVariable("formVersionId") Long formVersionId,
                                               @PathVariable("formEntityId") Long formEntityId) {
        formVersionFormEntityService.passiveFormEntity(formEntityId, formVersionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/form-versions/{formVersionId}/form-entities/list")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFormEntityList(@PathVariable Long formVersionId,
                                               @RequestBody FormEntityDto formEntityDto,
                                               @RequestParam int page, @RequestParam int size,
                                               @RequestParam(required = false) String orderBy,
                                               @RequestParam(required = false) Sort.Direction direction) {
        formEntityDto.setFormVersionId(formVersionId);
        SearchDto searchDto = formVersionFormEntityService.list(formEntityDto, page, size, orderBy, direction,
                formEntityService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
