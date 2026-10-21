package com.hamsoo.bpm.controller.approverlog;

import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.service.api.approverlog.ApproverLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ApproverLogController {
    private final ApproverLogService approverLogService;

    public ApproverLogController(ApproverLogService approverLogService) {
        this.approverLogService = approverLogService;
    }

    //----------get by id---------
    @GetMapping("/approver-log/{id}")
    @PreAuthorize("hasAnyResourceByModelApproveLogId(#id,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> getApproverLogById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(approverLogService.findByIdOrElseThrow(id), HttpStatus.OK);
    }

    //----------get All---------
    @GetMapping(value = "/approver-log/list")
    @PreAuthorize("hasAnyResource(@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<Page<?>> listAllApproverLog(@RequestParam int page, @RequestParam int size) {
        Page<ApproverLog> approverLogList = approverLogService.findAll(PageRequest.of(page, size));
        if (approverLogList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(approverLogList, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/approver-log/model/list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<?> listAllApproverLog(@PathVariable long modelVersionId ,
                                                      @RequestParam int page, @RequestParam int size) {
        SearchDto approverLogList = approverLogService.findAllByModelVersionId(modelVersionId,PageRequest.of(page, size));
         return new ResponseEntity<>(approverLogList, HttpStatus.OK);

    }

    //----------Save---------
    @PostMapping(value = "/approver-log", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyResourceByModelApproverId(#approverLog.modelApprover.id,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<Void> saveApproverLog(@RequestBody ApproverLog approverLog) {
        ApproverLog returnValue = approverLogService.save(approverLog);
        if (returnValue != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    //----------delete by id---------
    @DeleteMapping("/approver-log/{id}")
    @PreAuthorize("hasAnyResourceByModelApproveLogId(#id,@resourceItemService.MODEL_REFERENCE_MNG)")
    public ResponseEntity<ApproverLog> deleteApproverLog(
            @PathVariable("id") Long id) {
        approverLogService.deleteApproverLog(id);
        return new ResponseEntity<>(HttpStatus.OK);

    }


}
