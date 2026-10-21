package com.hamsoo.bpm.controller.modelrole;

import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ModelRoleController {

    private final ModelRoleService modelRoleService;

    public ModelRoleController(ModelRoleService modelRoleService) {
        this.modelRoleService = modelRoleService;
    }

    @GetMapping("/model-role/{id}")
    public ResponseEntity<?> getModelById(@PathVariable("id") Long id) {
        ModelRole modelRole = modelRoleService.findByIdOrElseThrow(id);
        ModelRoleDto modelRoleDto = modelRoleService.mapToDto(modelRole);
        return new ResponseEntity<>(modelRoleDto, HttpStatus.OK);
    }

    @GetMapping(value = "/model-role/list")
    public ResponseEntity<?> listAllModel() {
        List<ModelRoleDto> modelRoleDtoList = modelRoleService.list();
        return new ResponseEntity<>(modelRoleDtoList, HttpStatus.OK);
    }
}
