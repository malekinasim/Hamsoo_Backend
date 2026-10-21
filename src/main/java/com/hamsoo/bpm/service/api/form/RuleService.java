package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.RuleDto;
import com.hamsoo.bpm.model.entity.form.Rule;
import com.hamsoo.bpm.repository.jpa.form.RuleRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RuleService extends BaseService<Rule, Long, RuleRepository> {

    Rule create(RuleDto ruleDto);

    Rule edit(RuleDto ruleDto);

    void passive(Long id);

    RuleDto mapToDto(Rule rule);

    Optional<Rule> findByIdAndFormVersionId(Long id, Long formVersionId);

    Page<Rule> list(Long formVersionId, Pageable pageable);

    List<Rule> findByFormVersionId(Long formVersionId);

}
