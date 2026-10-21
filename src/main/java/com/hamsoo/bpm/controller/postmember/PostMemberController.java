package com.hamsoo.bpm.controller.postmember;

import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.postmember.PostMemberDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import com.hamsoo.bpm.validator.postmember.PostMemberValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_POST_MEMBER_MNG)")
public class PostMemberController {
    private final PostMemberService postMemberService;
    private final PostMemberValidator postMemberValidator;

    public PostMemberController(PostMemberService postMemberService, PostMemberValidator postMemberValidator) {
        this.postMemberService = postMemberService;
        this.postMemberValidator = postMemberValidator;
    }

    @InitBinder("postMemberDto")
    public void initPostMemberDtoBinder(WebDataBinder binder) {
        binder.setValidator(postMemberValidator);
    }


    @GetMapping("/post-member/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_POST_MEMBER_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        PostMember postMember = postMemberService.findByIdOrElseThrow(id);
        PostMemberDto postMemberDto = postMemberService.mapToDto(postMember);
        return new ResponseEntity<>(postMemberDto, HttpStatus.OK);
    }

    @PostMapping("/post-member")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> create(@Valid @RequestBody PostMemberDto postMemberDto) {
        PostMember postMember = postMemberService.create(postMemberDto);
        postMemberDto = postMemberService.mapToDto(postMember);
        return new ResponseEntity<>(postMemberDto, HttpStatus.OK);
    }

    @DeleteMapping("/post-member/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> passive(@PathVariable Long id) {
        postMemberService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/post-member/page")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_POST_MEMBER_MNG)")
    public ResponseEntity<?> pages(@RequestBody PostMemberDto postMemberDto,
                                   @RequestParam int page,
                                   @RequestParam int size,
                                   @RequestParam(required = false) String orderBy,
                                   @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = postMemberService.list(postMemberDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/post-member/list")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_POST_MEMBER_MNG)")
    public ResponseEntity<?> list(@RequestBody PostMemberDto postMemberDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = postMemberService.list(postMemberDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
