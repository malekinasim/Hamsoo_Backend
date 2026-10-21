package com.hamsoo.bpm.controller.identity;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.dto.identity.IoSupCustDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.validator.identity.IoSupCustValidator;
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
public class IoSupCustController {

    private final IoSupCustService ioSupCustService;
    private final IoSupCustValidator ioSupCustValidator;
    private final CodeCategoryService codeCategoryService;
    private final ModelVersionService modelVersionService;

    public IoSupCustController(IoSupCustService ioSupCustService,
                               IoSupCustValidator ioSupCustValidator,
                               CodeCategoryService codeCategoryService, ModelVersionService modelVersionService) {
        this.ioSupCustService = ioSupCustService;
        this.ioSupCustValidator = ioSupCustValidator;
        this.codeCategoryService = codeCategoryService;
        this.modelVersionService = modelVersionService;
    }

    @InitBinder("ioSupCustDto")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(ioSupCustValidator);
    }


    @PreAuthorize(value = "hasAnyResourceByIoSupCustId(#id,@resourceItemService.MODEL_CARD_INFO)")
    @GetMapping("/io-sup-custs/{id}")
    public ResponseEntity<?> findIoSupCustById(@PathVariable("id") Long id) {
        IoSupCust ioSupCust = ioSupCustService.findByIdOrElseThrow(id);
        IoSupCustDto ioSupCustDto = ioSupCustService.mapToDto(ioSupCust);
        return new ResponseEntity<>(ioSupCustDto, HttpStatus.OK);
    }

    @PreAuthorize(value = "hasAnyResourceByIdentityIoId(#ioSupCustDto.identityIoId,@resourceItemService.MODEL_CONS_SUP_EDIT)")
    @PostMapping("/io-sup-custs")
    public ResponseEntity<?> createIoSupCust(@Valid @RequestBody IoSupCustDto ioSupCustDto) {
        IoSupCust ioSupCust = ioSupCustService.create(ioSupCustDto);
        ioSupCustDto = ioSupCustService.mapToDto(ioSupCust);
        return new ResponseEntity<>(ioSupCustDto, HttpStatus.OK);
    }

    @PreAuthorize(value = "hasAnyResourceByIoSupCustId(#id,@resourceItemService.MODEL_CONS_SUP_EDIT)")
    @DeleteMapping("/io-sup-custs/{id}")
    public ResponseEntity<?> passiveIoSupCust(@PathVariable("id") Long id) {
        ioSupCustService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyResourceByIdentityIoId(#identityIoId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/io-sup-custs/list/{identityIoId}")
    public ResponseEntity<?> ioSupCustList(@PathVariable Long identityIoId,
                                           @RequestBody IoSupCustDto ioSupCustDto,
                                           @RequestParam int page, @RequestParam int size,
                                           @RequestParam(required = false) String orderBy,
                                           @RequestParam(required = false) Sort.Direction direction) {
        ioSupCustDto.setIdentityIoId(identityIoId);
        SearchDto searchDto = ioSupCustService.list(ioSupCustDto, page, size, orderBy, direction,
                ioSupCustService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyResourceByIdentityIoId(#identityIoId,@resourceItemService.MODEL_CARD_INFO)")
    @PostMapping("/io-sup-custs/list-with-additional-data/{identityIoId}")
    public ResponseEntity<?> ioSupCustListWithAdditionalData(@PathVariable Long identityIoId,
                                                             @RequestBody IoSupCustDto ioSupCustDto,
                                                             @RequestParam int page, @RequestParam int size,
                                                             @RequestParam(required = false) String orderBy,
                                                             @RequestParam(required = false) Sort.Direction direction) {
        ioSupCustDto.setIdentityIoId(identityIoId);
        SearchDto searchDto = ioSupCustService.list(ioSupCustDto, page, size, orderBy, direction,
                ioSupCustService::mapToDtoWithAdditionalData);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping("/io-sup-custs/type-list")
    public ResponseEntity<?> getIoSupCustTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(IoSupCust.Type.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }

    @PostMapping("/io-sup-custs/internal-process-list/{modelVersionId}")
    public ResponseEntity<?> getInternalProcessList(@PathVariable("modelVersionId") Long modelVersionId,
                                                    @RequestBody IoSupCustDto ioSupCustDto,
                                                    @RequestParam(required = false, value = "modelName") String modelName,
                                                    @RequestParam int page, @RequestParam int size) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        SearchDto searchDto = ioSupCustService.getInternalProcessList(modelVersion.getModel().getId(), ioSupCustDto,
                ioSupCustService.getInternalProcessAsisTobeCodeList(modelVersion), modelName, page, size);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
