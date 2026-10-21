package com.hamsoo.bpm.service.impl.chart;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.chart.ChartDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.chart.ChartRepository;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChartServiceImpl implements ChartService {
    private ChartRepository chartRepository;
    private OrganService organService;
    private JobService jobService;
    private UnitService unitService;

    public ChartServiceImpl(ChartRepository chartRepository, OrganService organService,
                            JobService jobService, @Lazy UnitService unitService) {
        this.chartRepository = chartRepository;
        this.organService = organService;
        this.jobService = jobService;
        this.unitService = unitService;
    }

    @Override
    public ChartRepository getRepository() {
        return chartRepository;
    }

    @Override
    public Chart findByIdOrElseThrow(Long id) {
        return getRepository().findByByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(() -> new BusinessException("error.chart.idIsNotValid"));
    }

    @Override
    public Chart create(ChartDto chartDto) {
        Assert.notNull(chartDto, "chartDto is null");
        Chart chart = new Chart();
        chart.setName(chartDto.getName());
        chart.setCode(chartDto.getCode());
        chart.setDescription(chartDto.getDescription());
        chart.setStatus(Status.ACTIVE.name());
        chart.setValidFrom(LocalDateTime.now());
        chart.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        return save(chart);
    }

    @Override
    public Chart edit(ChartDto chartDto) {
        Assert.notNull(chartDto, "chartDto is null");
        Chart chart = findByIdOrElseThrow(chartDto.getId());
        chart.setName(chartDto.getName());
        chart.setCode(chartDto.getCode());
        chart.setDescription(chartDto.getDescription());
        return save(chart);
    }


    @Override
    public void passive(Long id) {
        Chart chart = findByIdOrElseThrow(id);
        List<Job> jobs = jobService.findAllByChartId(chart.getId());
        if (!jobs.isEmpty())
            throw new BusinessException("error.chart.canNotDeleteByJob");

        List<Unit> units = unitService.findAllByChartId(chart.getId());
        if (!units.isEmpty())
            throw new BusinessException("error.chart.canNotDeleteByUnit");
        chart.setStatus(Status.PASSIVE.name());
        chart.setValidTo(LocalDateTime.now());
        save(chart);
    }

    @Override
    public Optional<Chart> findByCode(String code) {
        return getRepository().findByByCodeAndOrganId(code, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public ChartDto mapToDto(Chart chart) {
        ChartDto chartDto = new ChartDto();
        chartDto.setName(chart.getName());
        chartDto.setDescription(chart.getDescription());
        chartDto.setCode(chart.getCode());
        chartDto.setId(chart.getId());
        return chartDto;
    }

    @Override
    public SearchDto list(ChartDto chartDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Chart>> optionalSpecification = createSpecification(chartDto);
        Page<Chart> chartPage = findAll(optionalSpecification.get(), pageable);
        List<ChartDto> content = chartPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(chartPage.getTotalElements())
                .totalPages(chartPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<Chart>> createSpecification(ChartDto chartDto) {
        List<Specification<Chart>> specificationList = new ArrayList<>();
        Specification<Chart> organEq = (root, query, criteriaBuilder) -> {
            Join<Chart, Organ> organJoin = root.join(Chart_.ORGAN);
            Predicate[] predicates = new Predicate[3];

            predicates[0] = criteriaBuilder.equal(root.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[2] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());

            if (chartDto.getName() != null) {
                Predicate nameLike = criteriaBuilder.like(root.get(Chart_.NAME), "%" + chartDto.getName() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }
            if (chartDto.getCode() != null) {
                Predicate codeLike = criteriaBuilder.like(root.get(Chart_.CODE), "%" + chartDto.getCode() + "%");
                predicates = ArrayUtils.addAll(predicates, codeLike);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }
}
