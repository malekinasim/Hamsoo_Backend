package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.form.FunctionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.Function;
import com.hamsoo.bpm.service.api.form.FormVersionFunctionService;
import com.hamsoo.bpm.service.api.form.FunctionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FormVersionFunctionController {

    private final FormVersionFunctionService formVersionFunctionService;
    private final FunctionService functionService;

    public FormVersionFunctionController(FormVersionFunctionService formVersionFunctionService,
                                         FunctionService functionService) {
        this.formVersionFunctionService = formVersionFunctionService;
        this.functionService = functionService;
    }

    @GetMapping("/form-versions/{formVersionId}/functions/{functionId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findFunctionByIdAndFormVersionId(@PathVariable("formVersionId") Long formVersionId,
                                                              @PathVariable("functionId") Long functionId) {
        Function function = formVersionFunctionService.findByIdOrElseThrow(functionId, formVersionId);
        FunctionDto functionDto = functionService.mapToDto(function);
        return new ResponseEntity<>(functionDto, HttpStatus.OK);
    }

    @PostMapping("/form-versions/{formVersionId}/functions")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> addFunction(@PathVariable Long formVersionId,
                                         @Valid @RequestBody FunctionDto functionDto) {
        functionDto.setFormVersionId(formVersionId);
        Function function = formVersionFunctionService.addFunction(functionDto);
        functionDto = functionService.mapToDto(function);
        return new ResponseEntity<>(functionDto, HttpStatus.OK);
    }

    @PutMapping("/form-versions/{formVersionId}/functions")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editFunction(@PathVariable Long formVersionId,
                                          @Valid @RequestBody FunctionDto functionDto) {
        functionDto.setFormVersionId(formVersionId);
        Function function = formVersionFunctionService.editFunction(functionDto);
        functionDto = functionService.mapToDto(function);
        return new ResponseEntity<>(functionDto, HttpStatus.OK);
    }

    @DeleteMapping("/form-versions/{formVersionId}/functions/{functionId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> passiveFunction(@PathVariable("formVersionId") Long formVersionId,
                                             @PathVariable("functionId") Long functionId) {
        formVersionFunctionService.passiveFunction(functionId, formVersionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/form-versions/{formVersionId}/functions/list")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getFunctionList(@PathVariable Long formVersionId,
                                             @RequestBody FunctionDto functionDto,
                                             @RequestParam int page, @RequestParam int size,
                                             @RequestParam(required = false) String orderBy,
                                             @RequestParam(required = false) Sort.Direction direction) {
        functionDto.setFormVersionId(formVersionId);
        SearchDto searchDto = formVersionFunctionService.list(functionDto, page, size, orderBy, direction,
                functionService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
