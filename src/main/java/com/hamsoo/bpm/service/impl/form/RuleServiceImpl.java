package com.hamsoo.bpm.service.impl.form;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.form.RuleDto;
import com.hamsoo.bpm.model.entity.form.Rule;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.form.RuleRepository;
import com.hamsoo.bpm.service.api.form.FormLovCatalogService;
import com.hamsoo.bpm.service.api.form.RuleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final FormLovCatalogService formLovCatalogService;

    public RuleServiceImpl(RuleRepository ruleRepository,
                           FormLovCatalogService formLovCatalogService) {
        this.ruleRepository = ruleRepository;
        this.formLovCatalogService = formLovCatalogService;
    }

    @Override
    public RuleRepository getRepository() {
        return ruleRepository;
    }

    private Rule findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.rule.idIsNotValid");
        }
        return getRepository().findActiveById(id)
                .orElseThrow(() -> new BusinessException("error.rule.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Rule create(RuleDto ruleDto) {
        Assert.notNull(ruleDto, "ruleDto is null");
        Rule rule = new Rule();
        rule.setTitle(ruleDto.getTitle());
        rule.setDescription(ruleDto.getDescription());
        rule.setFromVersion(ruleDto.getFromVersion());
        rule.setType(formLovCatalogService.findByIdOrElseThrow(ruleDto.getTypeId()));
        rule.setStatus(Status.ACTIVE.name());
        return save(rule);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Rule edit(RuleDto ruleDto) {
        Assert.notNull(ruleDto, "ruleDto is null");
        Rule rule = findByIdOrElseThrow(ruleDto.getId());
        rule.setTitle(ruleDto.getTitle());
        rule.setDescription(ruleDto.getDescription());
        rule.setType(formLovCatalogService.findByIdOrElseThrow(ruleDto.getTypeId()));
        return save(rule);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        Rule rule = findByIdOrElseThrow(id);
        rule.setStatus(Status.PASSIVE.name());
        save(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDto mapToDto(Rule rule) {
        Assert.notNull(rule, "rule is null");
        rule = findByIdOrElseThrow(rule.getId());
        RuleDto ruleDto = new RuleDto();
        ruleDto.setId(rule.getId());
        ruleDto.setTitle(rule.getTitle());
        ruleDto.setDescription(rule.getDescription());
        ruleDto.setFromVersion(rule.getFromVersion());
        ruleDto.setType(formLovCatalogService.mapToDto(rule.getType()));
        return ruleDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rule> findByIdAndFormVersionId(Long id, Long formVersionId) {
        return getRepository().findByIdAndFormVersionId(id, formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Rule> list(Long formVersionId, Pageable pageable) {
        return getRepository().list(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rule> findByFormVersionId(Long formVersionId) {
        return getRepository().findByFormVersionId(formVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

}
