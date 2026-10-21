package com.hamsoo.bpm.controller.report;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.service.api.bpmnreport.BpmnReportService;
import com.hamsoo.bpm.service.api.model.ModelService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BpmnReportController {

    //private final ModelManagementService  modelManagementService;
    private final BpmnReportService bpmnReportService;
    private final ModelService modelService;



    public BpmnReportController(BpmnReportService bpmnReportService, ModelService modelService) {
        this.bpmnReportService = bpmnReportService;
        this.modelService = modelService;
    }

//    @GetMapping(value = "/bpmn-report/Model/Decisions")
//    public ResponseEntity<?> listModelDecisions() {
//        //  List<ModelTree> modelRoleDtoList = modelTreeService.getCurrentOrganTree(ModelVersion.AsisTobe.AS_IS.getCode());
//        List<BpmnReportDto> BpmnReportDtoList = bpmnReportService.getModelsDecisions();
//        return new ResponseEntity<>(BpmnReportDtoList, HttpStatus.OK);
//    }
//
//
//
//    @GetMapping(value = "/bpmn-report/Model/UserActivities")
//    public ResponseEntity<?> listUserActivities() {
//        //  List<ModelTree> modelRoleDtoList = modelTreeService.getCurrentOrganTree(ModelVersion.AsisTobe.AS_IS.getCode());
//        List<BpmnReportDto> BpmnReportDtoList = bpmnReportService.listUserActivities();
//        return new ResponseEntity<>(BpmnReportDtoList, HttpStatus.OK);
//    }

    @PostMapping(value = "/bpmn-report/Model/Decisions")
    public ResponseEntity<?> listModelDecisions(@RequestBody ModelDto modelDto,
                                          @RequestParam int page, @RequestParam int size,
                                          @RequestParam(required = false) String orderBy,
                                          @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelService.listModelDecisions(modelDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
