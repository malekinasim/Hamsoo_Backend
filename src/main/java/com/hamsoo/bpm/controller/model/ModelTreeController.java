package com.hamsoo.bpm.controller.model;

import com.hamsoo.bpm.model.entity.model.ModelTree;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.model.ModelTreeService;
import com.hamsoo.bpm.service.impl.model.ModelTreeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyAuthority(@resourceItemService.PROCESS_TREE)")
public class ModelTreeController {

    private ModelTreeService modelTreeService;

    public ModelTreeController(ModelTreeService modelTreeService) {
        this.modelTreeService = modelTreeService;
    }


    @GetMapping(value = "/model-tree/asis/list")
    public ResponseEntity<?> listAsisModelTree() {
        List<ModelTree> modelRoleDtoList = modelTreeService.getCurrentOrganTree(ModelVersion.AsisTobe.AS_IS.getCode());
        return new ResponseEntity<>(modelRoleDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/model-tree/tobe/list")
    public ResponseEntity<?> listTobeModelTree() {
        List<ModelTree> modelTreeList = modelTreeService.getCurrentOrganTree(ModelVersion.AsisTobe.TO_BE.getCode());
        return new ResponseEntity<>(modelTreeList, HttpStatus.OK);
    }

    @GetMapping(value = "/model-tree/statistics")
    public ResponseEntity<?> refrenceStatistics(@RequestParam(required = false) String AsisTobe) {
        ModelTreeServiceImpl.ModelStatisticsDto modelStatistics = modelTreeService.getModelStatistic(AsisTobe);//ModelVersion.AsisTobe.TO_BE.getCode());
        return new ResponseEntity<>(modelStatistics, HttpStatus.OK);
    }



}
