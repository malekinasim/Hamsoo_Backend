package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.RuleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.Rule;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public interface FormVersionRuleService {

    Rule findByIdOrElseThrow(Long ruleId, Long formVersionId);

    Rule addRule(RuleDto ruleDto);

    Rule editRule(RuleDto ruleDto);

    void passiveRule(Long ruleId, Long formVersionId);

    SearchDto list(RuleDto ruleDto, int page, int size, String orderBy, Sort.Direction direction, Function<Rule, RuleDto> mapper);

    void copyFormVersionRulesIfExist(Long formVersionIdFrom, Long formVersionIdTo);

}
