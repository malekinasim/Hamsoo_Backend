package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.form.RuleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.FormVersion;
import com.hamsoo.bpm.model.entity.form.Rule;
import com.hamsoo.bpm.service.api.form.FormVersionRuleService;
import com.hamsoo.bpm.service.api.form.FormVersionService;
import com.hamsoo.bpm.service.api.form.RuleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;

@Service
public class FormVersionRuleServiceImpl implements FormVersionRuleService {

    private final FormVersionService formVersionService;
    private final RuleService ruleService;

    public FormVersionRuleServiceImpl(FormVersionService formVersionService,
                                      RuleService ruleService) {
        this.formVersionService = formVersionService;
        this.ruleService = ruleService;
    }

    @Override
    @Transactional(readOnly = true)
    public Rule findByIdOrElseThrow(Long ruleId, Long formVersionId) {
        if (ruleId == null || formVersionId == null) {
            throw new BusinessException("error.formVersionRule.ruleIdNotFoundForThisFormVersion");
        }
        return ruleService.findByIdAndFormVersionId(ruleId, formVersionId)
                .orElseThrow(() -> new BusinessException("error.formVersionRule.ruleIdNotFoundForThisFormVersion"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Rule addRule(RuleDto ruleDto) {
        Assert.notNull(ruleDto, "ruleDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(ruleDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        ruleDto.setFromVersion(formVersion.getFormVersion());
        Rule rule = ruleService.create(ruleDto);
        formVersion.addRule(rule);
        formVersionService.save(formVersion);
        return rule;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Rule editRule(RuleDto ruleDto) {
        Assert.notNull(ruleDto, "ruleDto is null");
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(ruleDto.getFormVersionId());
        formVersionService.checkVersion(formVersion);
        Rule rule = findByIdOrElseThrow(ruleDto.getId(), formVersion.getId());
        Long formVersionCount = formVersionService.countByRuleId(ruleDto.getId());
        boolean editable = formVersionCount.equals(1L) && rule.getFromVersion().equals(formVersion.getFormVersion());
        if (!editable) {
            throw new BusinessException("error.formVersionRule.ruleNotEditable");
        }
        return ruleService.edit(ruleDto);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveRule(Long ruleId, Long formVersionId) {
        FormVersion formVersion = formVersionService.findByIdOrElseThrow(formVersionId);
        formVersionService.checkVersion(formVersion);
        if (CollectionUtils.isEmpty(formVersion.getRules())) {
            throw new BusinessException("error.formVersionRule.ruleIdNotFoundForThisFormVersion");
        }
        Rule rule = findByIdOrElseThrow(ruleId, formVersion.getId());
        if (!formVersion.getRules().contains(rule)) {
            throw new BusinessException("error.formVersionRule.ruleIdNotFoundForThisFormVersion");
        }
        formVersion.getRules().remove(rule);
        formVersionService.save(formVersion);
        Long formVersionCount = formVersionService.countByRuleId(rule.getId());
        if (formVersionCount.equals(0L)) {
            ruleService.passive(rule.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(RuleDto ruleDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<Rule, RuleDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<Rule> rulePage = ruleService.list(ruleDto.getFormVersionId(), pageable);
        List<RuleDto> content = rulePage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(rulePage.getTotalElements())
                .totalPages(rulePage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    /**
     * copy relations between formVersion and role entity
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyFormVersionRulesIfExist(Long formVersionIdFrom, Long formVersionIdTo) {
        List<Rule> ruleList = ruleService.findByFormVersionId(formVersionIdFrom);
        if (CollectionUtils.isEmpty(ruleList)) {
            return;
        }
        FormVersion formVersionTo = formVersionService.findByIdOrElseThrow(formVersionIdTo);
        ruleList.forEach(formVersionTo::addRule);
        formVersionService.save(formVersionTo);
    }

}
