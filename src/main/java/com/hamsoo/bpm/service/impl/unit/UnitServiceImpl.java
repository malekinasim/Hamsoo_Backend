package com.hamsoo.bpm.service.impl.unit;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.dto.unitcodehistory.UnitCodeHistoryDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.unit.Unit_;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.unit.UnitRepository;
import com.hamsoo.bpm.service.api.chart.ChartService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.unitcodehistory.UnitCodeHistoryService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final ChartService chartService;
    private final UnitCodeHistoryService unitCodeHistoryService;
    private final PostService postService;
    private final UserService userService;

    public UnitServiceImpl(UnitRepository unitRepository,
                           ChartService chartService,
                           UnitCodeHistoryService unitCodeHistoryService,
                           @Lazy PostService postService, UserService userService) {
        this.unitRepository = unitRepository;
        this.chartService = chartService;
        this.unitCodeHistoryService = unitCodeHistoryService;
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public UnitRepository getRepository() {
        return unitRepository;
    }

    @Override
    public Unit findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.unit.idIsNotValid");
        }
        return getRepository().findByIdAndOrganIdAndStatus(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.unit.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Unit create(UnitDto unitDto) {
        Assert.notNull(unitDto, "unitDto is null");
        Unit unit = new Unit();
        unit.setName(unitDto.getName());
        unit.setChart(chartService.findByIdOrElseThrow(unitDto.getChartId()));
        unit.setValidFrom(LocalDateTime.now());
        unit.setValidTo(null);
        unit.setStatus(Status.ACTIVE.name());
        unit.setSourceUnitId(unitDto.getSourceUnitId());
        if (unitDto.getParentId() != null)
            unit.setParentUnit(this.findByIdOrElseThrow(unitDto.getParentId()));
        unit = save(unit);

        UnitCodeHistoryDto unitCodeHistoryDto = new UnitCodeHistoryDto();
        unitCodeHistoryDto.setCode(unitDto.getCode());
        unitCodeHistoryDto.setUnitId(unit.getId());
        unitCodeHistoryService.create(unitCodeHistoryDto);
        return unit;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Unit edit(UnitDto unitDto) {
        Assert.notNull(unitDto, "unitDto is null");
        Unit unit = findByIdOrElseThrow(unitDto.getId());
        unit.setName(unitDto.getName());
        unit.setSourceUnitId(unitDto.getSourceUnitId());
        unit = save(unit);

        UnitCodeHistory activeUnitCodeHistory = unitCodeHistoryService.findActiveUnitCodeHistoryOrElseThrow(unit.getId(),
                SecurityUtils.getCurrentOrganIdOrElseThrow());
        if (!activeUnitCodeHistory.getCode().equals(unitDto.getCode())) {
            unitCodeHistoryService.passive(activeUnitCodeHistory.getId());
            UnitCodeHistoryDto unitCodeHistoryDto = new UnitCodeHistoryDto();
            unitCodeHistoryDto.setCode(unitDto.getCode());
            unitCodeHistoryDto.setUnitId(unit.getId());
            unitCodeHistoryService.create(unitCodeHistoryDto);
        }
        return unit;
    }

    @Override
    public void passive(Long id) {
        Unit unit = findByIdOrElseThrow(id);
        List<Unit> childUnits = this.getRepository().findAllByParentUnitIdAndChartId(unit.getId(), unit.getChart().getId(), SecurityUtils.getCurrentOrganIdOrElseThrow());
        if (!childUnits.isEmpty())
            throw new BusinessException("error.unit.canNotDeleteByChildUnit");

        List<Post> posts = postService.findAllByUnitId(unit.getId());
        if (!posts.isEmpty())
            throw new BusinessException("error.unit.canNotDeleteByPost");


        unit.setStatus(Status.PASSIVE.name());
        unit.setValidTo(LocalDateTime.now());
        unit = save(unit);
        UnitCodeHistory activeUnitCodeHistory = unitCodeHistoryService.findActiveUnitCodeHistoryOrElseThrow(unit.getId(),
                SecurityUtils.getCurrentOrganIdOrElseThrow());
        unitCodeHistoryService.passive(activeUnitCodeHistory.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UnitDto mapToDto(Unit unit) {
        Assert.notNull(unit, "unit is null");
        unit = findByIdOrElseThrow(unit.getId());
        UnitDto unitDto = new UnitDto();
        unitDto.setId(unit.getId());
        unitDto.setName(unit.getName());
        unitDto.setChartId(unit.getChart().getId());
        unitDto.setValidFrom(unit.getValidFrom());
        unitDto.setValidTo(unit.getValidTo());
        unitDto.setSourceUnitId(unit.getSourceUnitId());
        unitDto.setUserDtoList(userService.findAllUnitMember(unit.getId()).stream().map(u->userService.mapToDto(u)).toList());
        if (unit.getParentUnit() != null)
            unitDto.setParentId(unit.getParentUnit().getId());

        Optional<UnitCodeHistory> optionalActiveUnitCodeHistory =
                unitCodeHistoryService.findActiveUnitCodeHistory(unit.getId(), SecurityUtils.getCurrentOrganIdOrElseThrow());
        optionalActiveUnitCodeHistory.ifPresent(unitCodeHistory -> unitDto.setCode(unitCodeHistory.getCode()));


        return unitDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(UnitDto unitDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Unit>> optionalSpecification = createSpecification(unitDto);
        Page<Unit> unitPage = findAll(optionalSpecification.get(), pageable);
        List<UnitDto> content = unitPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(unitPage.getTotalElements())
                .totalPages(unitPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public Optional<Unit> findByCodeAndChartId(String code, Long chartId) {
        return this.getRepository().findByCodeAndChartIdAndOrganIdAndStatus(code, chartId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public List<Unit> findAllByChartId(Long chartId) {
        return this.getRepository().findAllByChartId(chartId);

    }

    private Optional<Specification<Unit>> createSpecification(UnitDto unitDto) {
        List<Specification<Unit>> specificationList = new ArrayList<>();

        Specification<Unit> organEq = (root, query, criteriaBuilder) -> {
            Join<Unit, Chart> chartJoin = root.join(Unit_.CHART);
            Join<Chart, Organ> organJoin = chartJoin.join(Chart_.ORGAN);
            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(root.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[2] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(root.get(Unit_.STATUS), Status.ACTIVE.name());

            if (unitDto.getName() != null) {
                Predicate nameLike = criteriaBuilder.like(root.get(Unit_.NAME), "%" + unitDto.getName() + "%");
                predicates = ArrayUtils.addAll(predicates, nameLike);
            }


            return criteriaBuilder.and(predicates);
        };
        if (unitDto.getCode() != null) {
            specificationList.add(unitCodeFilterExist(unitDto));
        }
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    private Specification<Unit> unitCodeFilterExist(UnitDto unitDto) {
        return (root, query, criteriaBuilder) -> {
            Subquery<UnitCodeHistory> subQuery = query.subquery(UnitCodeHistory.class);
            Root<UnitCodeHistory> subQueryRoot = subQuery.from(UnitCodeHistory.class);
            Join<UnitCodeHistory, Unit> unitJoin = subQueryRoot.join(UnitCodeHistory_.UNIT);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(UnitCodeHistory_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.like(subQueryRoot.get(UnitCodeHistory_.code), '%' + unitDto.getCode() + '%');
            predicates[3] = criteriaBuilder.equal(subQueryRoot.get(Unit_.ID), root.get(Unit_.ID));

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

}
