package com.hamsoo.bpm.controller.post;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.post.PostDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.validator.post.PostValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PostController {
    private final PostService postService;

    private final PostValidator postValidator;

    public PostController(PostService postService, PostValidator postValidator) {
        this.postService = postService;
        this.postValidator = postValidator;
    }

    @InitBinder("postDto")
    public void initPostBinder(WebDataBinder binder) {
        binder.setValidator(postValidator);
    }


    @GetMapping("/post/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_POST_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Post post = postService.findByIdOrElseThrow(id);
        PostDto postDto = postService.mapToDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @PostMapping("/post")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> create(@Valid @RequestBody PostDto postDto) {
        Post post = postService.create(postDto);
        postDto = postService.mapToDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @PutMapping("/post")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> edit(@Valid @RequestBody PostDto postDto) {
        Post post = postService.edit(postDto);
        postDto = postService.mapToDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }


    @DeleteMapping("/post/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        postService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/post/page")
     @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_POST_MNG)")
    public ResponseEntity<?> pages(@RequestBody PostDto postDto,
                                   @RequestParam int page,
                                   @RequestParam int size,
                                   @RequestParam(required = false) String orderBy,
                                   @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = postService.list(postDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/post/list")
    @PreAuthorize(value = "hasAnyResource(@resourceItemService.ORGANIZATIONAL_CHART_POST_MNG)")
    public ResponseEntity<?> list(@RequestBody PostDto postDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = postService.list(postDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }

}
