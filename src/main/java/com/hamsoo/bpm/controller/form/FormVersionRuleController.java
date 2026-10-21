package com.hamsoo.bpm.controller.form;

import com.hamsoo.bpm.model.dto.form.RuleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.Rule;
import com.hamsoo.bpm.service.api.form.FormVersionRuleService;
import com.hamsoo.bpm.service.api.form.RuleService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FormVersionRuleController {

    private final FormVersionRuleService formVersionRuleService;
    private final RuleService ruleService;

    public FormVersionRuleController(FormVersionRuleService formVersionRuleService,
                                     RuleService ruleService) {
        this.formVersionRuleService = formVersionRuleService;
        this.ruleService = ruleService;
    }

    @GetMapping("/form-versions/{formVersionId}/rules/{ruleId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> findRoleByIdAndFormVersionId(@PathVariable("formVersionId") Long formVersionId,
                                                          @PathVariable("ruleId") Long ruleId) {
        Rule rule = formVersionRuleService.findByIdOrElseThrow(ruleId, formVersionId);
        RuleDto ruleDto = ruleService.mapToDto(rule);
        return new ResponseEntity<>(ruleDto, HttpStatus.OK);
    }

    @PostMapping("/form-versions/{formVersionId}/rules")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> addRule(@PathVariable Long formVersionId,
                                     @Valid @RequestBody RuleDto ruleDto) {
        ruleDto.setFormVersionId(formVersionId);
        Rule rule = formVersionRuleService.addRule(ruleDto);
        ruleDto = ruleService.mapToDto(rule);
        return new ResponseEntity<>(ruleDto, HttpStatus.OK);
    }

    @PutMapping("/form-versions/{formVersionId}/rules")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> editRule(@PathVariable Long formVersionId,
                                      @Valid @RequestBody RuleDto ruleDto) {
        ruleDto.setFormVersionId(formVersionId);
        Rule rule = formVersionRuleService.editRule(ruleDto);
        ruleDto = ruleService.mapToDto(rule);
        return new ResponseEntity<>(ruleDto, HttpStatus.OK);
    }

    @DeleteMapping("/form-versions/{formVersionId}/rules/{ruleId}")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> passiveRule(@PathVariable("formVersionId") Long formVersionId,
                                         @PathVariable("ruleId") Long ruleId) {
        formVersionRuleService.passiveRule(ruleId, formVersionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/form-versions/{formVersionId}/rules/list")
//    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.TO-DO)")
    public ResponseEntity<?> getRuleList(@PathVariable Long formVersionId,
                                         @RequestBody RuleDto ruleDto,
                                         @RequestParam int page, @RequestParam int size,
                                         @RequestParam(required = false) String orderBy,
                                         @RequestParam(required = false) Sort.Direction direction) {
        ruleDto.setFormVersionId(formVersionId);
        SearchDto searchDto = formVersionRuleService.list(ruleDto, page, size, orderBy, direction,
                ruleService::mapToDto);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

}
