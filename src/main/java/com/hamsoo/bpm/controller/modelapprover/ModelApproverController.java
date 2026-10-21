package com.hamsoo.bpm.controller.modelapprover;

import com.hamsoo.bpm.model.dto.codecategory.CodeCategoryDto;
import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverChangeOrderRQ;
import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ModelApproverController {
    private final ModelApproverService modelApproverService;
    private final CodeCategoryService codeCategoryService;

    public ModelApproverController(ModelApproverService modelApproverService,
                                   CodeCategoryService codeCategoryService) {
        this.modelApproverService = modelApproverService;
        this.codeCategoryService = codeCategoryService;
    }


    //----------get All---------
    @GetMapping(value = "/model-approver/list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<List<?>> listAllModelApprover(@PathVariable(name = "modelVersionId") Long modelVersionId) {
        List<ModelApproverDto> modelApproverDtoList = modelApproverService.list(modelVersionId, null);
        return new ResponseEntity<>(modelApproverDtoList, HttpStatus.OK);
    }


    //----------get All Approver of group---------
    @GetMapping(value = "/model-approver/list/{modelVersionId}/{approverGroupCode}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<List<?>> listAllModelApproverOfGroup(@PathVariable(name = "modelVersionId") Long modelVersionId,
                                                               @PathVariable(name = "modelVersionId") String approverGroupCode) {
        List<ModelApproverDto> modelApproverDtoList = modelApproverService.list(modelVersionId, approverGroupCode);
        return new ResponseEntity<>(modelApproverDtoList, HttpStatus.OK);
    }


    //----------SaveDefaultList---------
    @PostMapping(value = "/model-approver-create-default-list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> saveDefaultModelApproverList(@PathVariable(name = "modelVersionId") Long modelVersionId) {
        List<ModelApproverDto> modelApproverDtoList = modelApproverService.generateDefaultModelApproverList(modelVersionId);
        return new ResponseEntity<>(modelApproverDtoList, HttpStatus.CREATED);
    }

    //----------Save---------
    @PostMapping(value = "/model-approver", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelApproverDto.getModelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> saveModelApprover(@RequestBody ModelApproverDto modelApproverDto) {
        ModelApprover modelApprover = modelApproverService.create(modelApproverDto);
        modelApproverDto = modelApproverService.mapToDto(modelApprover);
        return new ResponseEntity<>(modelApproverDto, HttpStatus.CREATED);
    }

    //----------edit---------
    @PutMapping("/model-approver")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelApproverDto.getModelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> editModelApprover(@RequestBody ModelApproverDto modelApproverDto) {
        modelApproverDto = modelApproverService.edit(modelApproverDto);
        return new ResponseEntity<>(modelApproverDto, HttpStatus.OK);
    }

    //----------changeOrder---------
    @PutMapping("/model-approver-change-order")
    @PreAuthorize("hasAnyResourceByModelVersionId(#request.getModelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<List<?>> ChangeModelApproverOrder(@RequestBody ModelApproverChangeOrderRQ request) {
        List<ModelApproverDto> modelApproverDtoList = modelApproverService.ChangeModelApproverOrder(request.getModelVersionId(), request.getSrcModelApprover(), request.getTaregetModelApprover());

        return new ResponseEntity<>(modelApproverDtoList, HttpStatus.OK);
    }

    //----------passive---------
    @DeleteMapping("/model-approver/{id}")
    @PreAuthorize("hasAnyResourceByModelApproverId(#id,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> passiveModelApprover(@PathVariable("id") Long id) {
        modelApproverService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/model-approver/approve-level-type-list")
    @PreAuthorize("hasAnyResource(@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> geApproveLevelTypeList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(ModelApprover.ApproveLevelType.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }

    @GetMapping("/model-approver/approve-group-type-list")
    @PreAuthorize("hasAnyResource(@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> getApproveModelList() {
        List<CodeCategory> codeCategoryList = codeCategoryService.findByCategory(ModelApprover.ApproveGroupType.CATEGORY);
        List<CodeCategoryDto> codeCategoryDtoList = codeCategoryService.mapToDto(codeCategoryList);
        return new ResponseEntity<>(codeCategoryDtoList, HttpStatus.OK);
    }

    @PostMapping("/model-approver/user-model-list")
    @PreAuthorize("hasAnyResource(@resourceItemService.REFERENCE_MODELS_LIST)")
    public ResponseEntity<?> getApproveModelList(@RequestBody ModelApproverDto modelApproverDto,
                                                 @RequestParam int page, @RequestParam int size) {
        SearchDto searchDto = modelApproverService.getModelApproveListForUser(modelApproverDto, page, size);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
