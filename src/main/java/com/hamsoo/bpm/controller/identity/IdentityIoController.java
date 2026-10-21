package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.dto.identity.IdentityIoDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.validator.identity.IdentityIoValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IdentityIoController {

    private final IdentityIoService identityIoService;
    private final IdentityIoValidator identityIoValidator;
    private final CodeCategoryService codeCategoryService;

    public IdentityIoController(IdentityIoService identityIoService,
                                IdentityIoValidator identityIoValidator,
                                CodeCategoryService codeCategoryService) {
        this.identityIoService = identityIoService;
        this.identityIoValidator = identityIoValidator;
        this.codeCategoryService = codeCategoryService;
    }

    @InitBinder("identityIoDto")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(identityIoValidator);
    }


    @GetMapping("/identity-ios/{id}")
    @PreAuthorize(value = "hasAnyResourceByIdentityIoId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> findIdentityIoById(@PathVariable("id") Long id) {
        IdentityIo identityIo = identityIoService.findByIdOrElseThrow(id);
        IdentityIoDto identityIoDto = identityIoService.mapToDto(identityIo);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }

    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#identityIoDto.modelVersionId,@resourceItemService.MODEL_INPUT_OUTPUT_EDIT)")
    @PostMapping("/identity-ios")
    public ResponseEntity<?> createIdentityIo(@Valid @RequestBody IdentityIoDto identityIoDto) {
        IdentityIo identityIo = identityIoService.create(identityIoDto);
        identityIoDto = identityIoService.mapToDto(identityIo);
        return new ResponseEntity<>(identityIoDto, HttpStatus.OK);
    }

    @PreAuthorize(value = "hasAnyResourceByIdentityIoId(#id,@resourceItemService.MODEL_INPUT_OUTPUT_EDIT)")
    @DeleteMapping("/identity-ios/{id}")
    public ResponseEntity<?> passiveIdentityIo(@PathVariable("id") Long id) {
        identityIoService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-ios/list/{modelVersionId}")
    public ResponseEntity<?> identityIoList(@PathVariable Long modelVersionId,
                                            @RequestBody IdentityIoDto identityIoDto,
                                            @RequestParam int page, @RequestParam int size,
                                            @RequestParam(required = false) String orderBy,
                                            @RequestParam(required = false) Sort.Direction direction) {
        identityIoDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = identityIoService.list(identityIoDto, page, size, orderBy, direction,
                identityIoService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/identity-ios/list-with-additional-data/{modelVersionId}")
    public ResponseEntity<?> identityIoListWithAdditionalData(@PathVariable Long modelVersionId,
                                                              @RequestBody IdentityIoDto identityIoDto,
                                                              @RequestParam int page, @RequestParam int size,
                                                              @RequestParam(required = false) String orderBy,
                                                              @RequestParam(required = false) Sort.Direction direction) {
        identityIoDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = identityIoService.list(identityIoDto, page, size, orderBy, direction,
                identityIoService::mapToDtoWithAdditionalData);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping("/identity-ios/type-list")
    public ResponseEntity<?> getIdentityIoTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(IdentityIo.Type.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }
}
