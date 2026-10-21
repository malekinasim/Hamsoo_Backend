package com.hamsoo.bpm.controller.modelelementreference;

import com.hamsoo.bpm.model.dto.modelelementereference.ModelAssigneeDto;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementTaskAssigneeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ModelElementTaskAssigneeController {

    private final ModelElementTaskAssigneeService modelElementTaskAssigneeService;

    public ModelElementTaskAssigneeController(ModelElementTaskAssigneeService modelElementTaskAssigneeService) {
        this.modelElementTaskAssigneeService = modelElementTaskAssigneeService;
    }


    @PostMapping(value = "/model-element-task-assignee")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelTaskAssigneeDto.modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> createModelElementTaskAssignee(@Valid @RequestBody ModelAssigneeDto modelTaskAssigneeDto) {
        modelTaskAssigneeDto = modelElementTaskAssigneeService.create(modelTaskAssigneeDto);
        return new ResponseEntity<>(modelTaskAssigneeDto, HttpStatus.OK);

    }

    @PutMapping(value = "/model-element-task-assignee/lan_assignee/{modelVersionId}/{laneId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> editModelElementLaneAssignee(@PathVariable Long modelVersionId, @PathVariable String laneId) {
        modelElementTaskAssigneeService.editModelLanAssignee(laneId, modelVersionId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PutMapping(value = "/model-element-task-assignee/lan_assignee/{modelVersionId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> editModelElementLaneAssignee(@PathVariable Long modelVersionId) {
        modelElementTaskAssigneeService.editAllModelLaneAssignee(modelVersionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/model-element-task-assignee")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelTaskAssigneeDto.modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> editModelElementTaskAssignee(@Valid @RequestBody ModelAssigneeDto modelTaskAssigneeDto) {
        modelTaskAssigneeDto = modelElementTaskAssigneeService.edit(modelTaskAssigneeDto);
        return new ResponseEntity<>(modelTaskAssigneeDto, HttpStatus.OK);

    }

    @DeleteMapping("/model-element-task-assignee/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelElementReferenceId(#id,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> deleteModelElementTaskAssigneeById(@PathVariable Long id) {
        modelElementTaskAssigneeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/model-element-task-assignee/all-element-assignee/{modelVersionId}/{elementId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> deleteAllModelElementTaskAssigneeByModelVersionIdAndElementId(@PathVariable Long modelVersionId, @PathVariable String elementId) {
        modelElementTaskAssigneeService.deleteAllModelElementAssigneeByModelVersionIdAndElementId(modelVersionId, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/model-element-task-assignee/list/{modelVersionId}/{elementId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_TASK_ASSIGN_MNG)")
    public ResponseEntity<?> getElementTaskAssigneeList(@PathVariable Long modelVersionId, @PathVariable String elementId) {
        ModelAssigneeDto modelAssigneeDto = modelElementTaskAssigneeService.getElementTaskAssigneeListOfLastVersion(modelVersionId, elementId);
        return new ResponseEntity<>(modelAssigneeDto, HttpStatus.OK);
    }
}
