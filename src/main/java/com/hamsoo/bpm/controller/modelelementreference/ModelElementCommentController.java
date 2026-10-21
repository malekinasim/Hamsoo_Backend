package com.hamsoo.bpm.controller.modelelementreference;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementCommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ModelElementCommentController {

    private final ModelElementCommentService modelElementCommentService;

    public ModelElementCommentController(ModelElementCommentService modelElementCommentService) {
        this.modelElementCommentService = modelElementCommentService;
    }

    @PostMapping(value = "/model-element-comments")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelCommentDto.modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> createModelElementComment(@Valid @RequestBody ModelCommentDto modelCommentDto) {
        modelCommentDto = modelElementCommentService.create(modelCommentDto);
        return new ResponseEntity<>(modelCommentDto, HttpStatus.OK);
    }

    @DeleteMapping("/model-element-comments/{id}")
    @PreAuthorize("hasAnyResourceByModelElementReferenceId(#id,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> deleteModelElementCommentById(@PathVariable Long id) {
        modelElementCommentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/model-element-comments/all-element-comment/{modelVersionId}/{elementId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> deleteAllModelElementCommentByModelVersionIdAndElementId(@PathVariable Long modelVersionId, @PathVariable String elementId) {
        modelElementCommentService.deleteAllModelElementCommentByModelVersionIdAndElementId(modelVersionId, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = "/model-element-comments/list/{modelVersionId}/{elementId}")
    @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_COMMENT_MNG)")
    public ResponseEntity<?> getElementCommentList(@PathVariable Long modelVersionId, @PathVariable String elementId,
                                                   @RequestParam(required = false) String orderBy,
                                                   @RequestParam(required = false) Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        SearchDto searchDto = modelElementCommentService.getElementCommentListOfLastVersionWithPageData(modelVersionId, elementId, pageable);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }


}
