package com.hamsoo.bpm.validator.postmember;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.postmember.PostMemberDto;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.person.PersonService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class PostMemberValidator extends HamsooValidator {
    private PersonService personService;
    private PostService postService;
    private PostMemberService postMemberService;
    private ChartService chartService;

    public PostMemberValidator(PersonService personService,
                               PostService postService,
                               PostMemberService postMemberService,
                               ChartService chartService) {
        this.personService = personService;
        this.postService = postService;
        this.postMemberService = postMemberService;
        this.chartService = chartService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PostMemberDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        PostMemberDto postMemberDto = (PostMemberDto) target;
        //create Validator
        if (postMemberDto.getId() == null) {
            if (postMemberDto.getPostId() == null) {
                errors.rejectValue("postId", "", "error.postMember.postIdsNotValid");
            }
            if (postMemberDto.getPersonId() == null) {
                errors.rejectValue("personId", "", "error.postMember.personIdsNotValid");
            }

            if (postMemberDto.getChartId() == null) {
                errors.rejectValue("chartId", "", "error.postMember.chartIdsNotValid");
            }


            try {
                postService.findByIdOrElseThrow(postMemberDto.getPostId());
            } catch (BusinessException e) {
                errors.rejectValue("postId", "", "error.postMember.postIdsNotValid");
            }

            try {
                personService.findByIdAndOrganOrElseThrow(postMemberDto.getPersonId(), SecurityUtils.getCurrentOrganIdOrElseThrow());
            } catch (BusinessException e) {
                errors.rejectValue("personId", "", "error.postMember.personIdsNotValid");
            }
            try {
                chartService.findByIdOrElseThrow(postMemberDto.getChartId());
            } catch (BusinessException e) {
                errors.rejectValue("chartId", "", "error.postMember.chartIdsNotValid");
            }

            Optional<PostMember> postMember = postMemberService.findByPersonIdAndPostIdAndOrganIdAndChartId(postMemberDto.getPersonId(), postMemberDto.getPostId(), postMemberDto.getChartId());
            if (postMember.isPresent()) {
                errors.rejectValue("id", "", "error.postMember.postMemberIsDuplicate");
            }
        }
    }
}
