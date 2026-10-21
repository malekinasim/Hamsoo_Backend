package com.hamsoo.bpm.controller.modelapprover;

import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.modelapprover.StartProcessResDTO;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ModelApproverProcessController {
    private final ModelApproverProcessService modelApproverProcessService;

    public ModelApproverProcessController(ModelApproverProcessService modelApproverProcessService) {
        this.modelApproverProcessService = modelApproverProcessService;
    }

    //----------SaveDefaultList---------
    @PutMapping(value = "/model-approver-process-start/{modelVersionId}/{force}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> statrtProcess(@PathVariable(name = "modelVersionId") Long modelVersionId, @PathVariable(name = "force") Boolean force) {
        StartProcessResDTO result = modelApproverProcessService.startProcess(modelVersionId, force);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value = "/model-approver-process-approve/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_APPROVER)")
    public ResponseEntity<?> Approve(@PathVariable(name = "modelVersionId") Long modelVersionId, @RequestBody ModelApproverDto modelApproverDto) {
        ModelApproverDto result = modelApproverProcessService.approveModel(modelVersionId, modelApproverDto);
        if (result == null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value = "/model-approver-process-defer/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_DEFER)")
    public ResponseEntity<?> defer(@PathVariable(name = "modelVersionId") Long modelVersionId, @RequestBody ModelApproverDto modelApproverDto) {
        ModelApproverDto result = modelApproverProcessService.deferModel(modelVersionId, modelApproverDto);
        if (result == null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value = "/model-approver-process-acknowledge/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_ACKNOWLEDGE)")
    public ResponseEntity<?> acknowledge(@PathVariable(name = "modelVersionId") Long modelVersionId, @RequestBody ModelApproverDto modelApproverDto) {
        ModelApproverDto result = modelApproverProcessService.acknowledgeModel(modelVersionId, modelApproverDto);
        if (result == null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
