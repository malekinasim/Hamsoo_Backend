package com.hamsoo.bpm.controller.modelcomment;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")

public class ModelCommentController {

    private final ModelCommentService modelCommentService;

    public ModelCommentController(ModelCommentService modelCommentService) {
        this.modelCommentService = modelCommentService;
    }

    @GetMapping("/model-comment/{id}")
    @PreAuthorize("hasAnyResourceByModelCommentId(#idd,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> findModelCommentById(@PathVariable("id") Long id) {
        ModelComment modelComment = modelCommentService.findByIdOrElseThrow(id);
        ModelCommentDto modelCommentDto = modelCommentService.mapToDto(modelComment);
        return new ResponseEntity<>(modelCommentDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-comment")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelCommentDto.modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> createModelComment(@Valid @RequestBody ModelCommentDto modelCommentDto) {
        ModelComment modelComment = modelCommentService.create(modelCommentDto);
        modelCommentDto = modelCommentService.mapToDto(modelComment);
        return new ResponseEntity<>(modelCommentDto, HttpStatus.CREATED);
    }

    @PutMapping("/model-comment")
    @PreAuthorize("hasAnyResourceByModelCommentId(#modelCommentDto.id,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> editModelComment(@Valid @RequestBody ModelCommentDto modelCommentDto) {
        ModelComment modelComment = modelCommentService.edit(modelCommentDto);
        modelCommentDto = modelCommentService.mapToDto(modelComment);
        return new ResponseEntity<>(modelCommentDto, HttpStatus.OK);
    }

    @DeleteMapping("/model-comment/{id}")
    @PreAuthorize("hasAnyResourceByModelCommentId(#id,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> passiveModelComment(@PathVariable Long id) {
        modelCommentService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/model-comment/list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> modelCommentList(@PathVariable Long modelVersionId, @RequestBody ModelCommentDto modelCommentDto,
                                              @RequestParam int page, @RequestParam int size,
                                              @RequestParam(required = false) String orderBy,
                                              @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelCommentService.list(modelVersionId, modelCommentDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-comment/comment-and-element-comment-list/{modelVersionId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> getCommentAndElementCommentList(@PathVariable Long modelVersionId,
                                                             @RequestParam int page, @RequestParam int size,
                                                             @RequestParam(required = false) String orderBy,
                                                             @RequestParam(required = false) Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        SearchDto searchDto = modelCommentService.getCommentAndElementCommentListOfLastVersionWithPageData(modelVersionId, pageable);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }


}
