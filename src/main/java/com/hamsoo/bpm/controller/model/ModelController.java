package com.hamsoo.bpm.controller.model;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.model.ModelService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/Model/{id}")
    @PreAuthorize(value="hasAnyResourceByModelId(#id,@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> getModelById(@PathVariable("id") Long id) {
        Model model = modelService.findByIdOrElseThrow(id);
        ModelDto modelDto = modelService.mapToDto(model);
        return new ResponseEntity<>(modelDto, HttpStatus.OK);
    }

    @GetMapping("/ModelWithLastVersionData/{id}")
    @PreAuthorize(value="hasAnyResourceByModelId(#id,@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> getModelWithLastVersionDataById(@PathVariable("id") Long id) {
        Model model = modelService.findByIdOrElseThrow(id);
        ModelDto modelDto = modelService.mapToDtoWithLastVersionData(model);
        return new ResponseEntity<>(modelDto, HttpStatus.OK);
    }

    @PostMapping(value = "/Model/list")
    public ResponseEntity<?> listAllModel(@RequestBody ModelDto modelDto,
                                          @RequestParam int page, @RequestParam int size,
                                          @RequestParam(required = false) String orderBy,
                                          @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelService.list(modelDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
    @DeleteMapping("/Model/{id}")
    @PreAuthorize(value="hasAnyResourceByModelId(#id,@resourceItemService.MODEL_REMOVE)")
    public ResponseEntity<?> passiveModel(@PathVariable Long id) {
        modelService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/Model/rename/{id}")
    @PreAuthorize(value="hasAnyResourceByModelId(#id,@resourceItemService.MODEL_RENAME)")
    public ResponseEntity<?> renameModel(@PathVariable("id") Long id, @RequestBody ModelDto modelDto) {
        if (modelDto == null || !StringUtils.hasText(modelDto.getName())) {
            throw new BusinessException("error.model.nameIsNotValid");
        }
        Model model = modelService.rename(id, modelDto.getName());
        modelDto = modelService.mapToDto(model);
        return new ResponseEntity<>(modelDto, HttpStatus.OK);
    }

    @PutMapping("/Model/ToggleBookmarkState/{id}")
    public ResponseEntity<?> toggleBookmarkState(@PathVariable("id") Long id) {
        Boolean bookmarkState = modelService.toggleBookmarkState(id);
        return new ResponseEntity<>(bookmarkState, HttpStatus.OK);
    }

    @PostMapping(value = "/Model/GatewaysWithLane/list")
    @PreAuthorize(value="hasAnyAuthority(@resourceItemService.MODEL_DECISION_REPORT)")
    public ResponseEntity<?> listModelGatewaysWithLane(@RequestBody ModelDto modelDto,
                                               @RequestParam int page, @RequestParam int size,
                                               @RequestParam(required = false) String orderBy,
                                               @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelService.listModelDecisions(modelDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/Model/singleUser/modellist")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGAN_MEMBER_MNG)")
    public ResponseEntity<?> listSingleUserModels(@RequestBody ModelDto modelDto,
                                                  @RequestParam int page, @RequestParam int size,
                                                  @RequestParam(required = false) String orderBy,
                                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelService.listSingleUserModels(modelDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }
}
