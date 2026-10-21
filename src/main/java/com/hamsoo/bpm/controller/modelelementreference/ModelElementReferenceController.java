package com.hamsoo.bpm.controller.modelelementreference;

import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.validator.modelelementreference.ModelElementReferenceValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ModelElementReferenceController {
    private ModelElementReferenceService modelElementReferenceService;
    private ModelElementReferenceValidator modelElementReferenceValidator;


    public ModelElementReferenceController(ModelElementReferenceService modelElementReferenceService, ModelElementReferenceValidator modelElementReferenceValidator) {
        this.modelElementReferenceService = modelElementReferenceService;
        this.modelElementReferenceValidator = modelElementReferenceValidator;
    }

    @InitBinder("modelElementReferenceDto")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(modelElementReferenceValidator);
    }

    @PostMapping("/model_element_reference")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelElementReferenceDto.modelVersionId,@resourceItemService.MODEL_TREE_MNG)")
    public ResponseEntity<?> createOrUpdateModelElementReference(@Valid @RequestBody ModelElementReferenceDto modelElementReferenceDto) {
        ModelElementRefrence modelElementRefrence = modelElementReferenceService.create(modelElementReferenceDto);
        modelElementReferenceDto = modelElementReferenceService.mapToDto(modelElementRefrence);
        return new ResponseEntity<>(modelElementReferenceDto, HttpStatus.CREATED);
    }


    @PutMapping("/model_element_reference")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelElementReferenceDto.modelVersionId,@resourceItemService.MODEL_TREE_MNG)")
    public ResponseEntity<?> editModelElementReference(@Valid @RequestBody ModelElementReferenceDto modelElementReferenceDto) {
        ModelElementRefrence modelElementRefrence = modelElementReferenceService.edit(modelElementReferenceDto);
        modelElementReferenceDto = modelElementReferenceService.mapToDto(modelElementRefrence);
        return new ResponseEntity<>(modelElementReferenceDto, HttpStatus.OK);
    }

    @GetMapping("/model_element_reference/{id}")
    @PreAuthorize("hasAnyResourceByModelElementReferenceId(#id,@resourceItemService.MODEL_TREE_MNG)")
    public ResponseEntity<?> getModelElementReferenceById(@PathVariable("id") Long id) {
        ModelElementRefrence modelElementRefrence = modelElementReferenceService.findByIdOrElseThrow(id);
        ModelElementReferenceDto modelElementReferenceDto = modelElementReferenceService.mapToDto(modelElementRefrence);
        return new ResponseEntity<>(modelElementReferenceDto, HttpStatus.OK);
    }

    @DeleteMapping("/model_element_reference/{id}")
    @PreAuthorize("hasAnyResourceByModelElementReferenceId(#id,@resourceItemService.MODEL_TREE_MNG)")
    public ResponseEntity<?> deleteAllElementReferenceOnModelVersion(@PathVariable("id") Long id) {
        modelElementReferenceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/model_element_reference/modelVersion/elements/{modelVersionId:^[1-9]+[0-9]*$}/{elementId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_TREE_MNG," +
            "@resourceItemService.MODEL_COMMENT_MNG,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> deleteAllElementReferenceOnModelVersion(@PathVariable(value = "modelVersionId") Long modelVersionId,
                                                                     @PathVariable(value = "elementId") String elementId) {
        modelElementReferenceService.deleteAllReferenceByElementIdAndModelVersion(modelVersionId, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/model_element_reference/modelVersion/list")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelElementReferenceDto.modelVersionId,@resourceItemService.MODEL_TREE_MNG," +
            "@resourceItemService.MODEL_COMMENT_MNG,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> AllModelElementReferenceByModelVersionIdAndReferenceTypeList(@RequestBody ModelElementReferenceDto modelElementReferenceDto) {
        List<ModelElementReferenceDto> modelElementReferenceDtos = modelElementReferenceService.list(modelElementReferenceDto);
        return new ResponseEntity<>(modelElementReferenceDtos, HttpStatus.OK);
    }


    @PostMapping("/model_element_reference/internal-process-list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelElementReferenceDto.modelVersionId,@resourceItemService.MODEL_TREE_MNG)")
    public ResponseEntity<?> getInternalProcessList(@PathVariable("modelVersionId") Long modelVersionId,
                                                    @RequestBody ModelElementReferenceDto modelElementReferenceDto,
                                                    @RequestParam(required = false, value = "modelName") String modelName,
                                                    @RequestParam int page, @RequestParam int size) {

        SearchDto searchDto = modelElementReferenceService.getInternalProcessList(modelVersionId, modelElementReferenceDto, modelName, page, size);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
