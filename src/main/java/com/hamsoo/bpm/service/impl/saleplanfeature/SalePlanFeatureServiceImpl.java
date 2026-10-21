package com.hamsoo.bpm.service.impl.saleplanfeature;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.service.api.feature.FeatureService;
import com.hamsoo.bpm.service.api.saleplan.SalePlanService;
import com.hamsoo.bpm.service.api.saleplanfeature.SalePlanFeatureService;
import com.hamsoo.bpm.model.entity.feature.Feature_;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan_;
import com.hamsoo.bpm.model.entity.saleplanfeature.SalePlanFeature;
import com.hamsoo.bpm.model.entity.saleplanfeature.SalePlanFeature_;
import com.hamsoo.bpm.repository.jpa.saleplanfeature.SalePlanFeatureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SalePlanFeatureServiceImpl implements SalePlanFeatureService {
    private SalePlanFeatureRepository salePlanFeatureRepository;
    private FeatureService featureService;
    private SalePlanService salePlanService;

    public SalePlanFeatureServiceImpl(SalePlanFeatureRepository salePlanFeatureRepository, FeatureService featureService, SalePlanService salePlanService) {
        this.salePlanFeatureRepository = salePlanFeatureRepository;
        this.featureService = featureService;
        this.salePlanService = salePlanService;
    }

    @Override
    public SalePlanFeatureRepository getRepository() {
        return salePlanFeatureRepository;
    }

    @Override
    public SalePlanFeature findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new BusinessException("error.salePlanFeature.notFound"));
    }

    @Override
    public SalePlanFeature create(SalePlanFeatureDto salePlanFeatureDto) {
        SalePlanFeature salePlanFeature = new SalePlanFeature();
        salePlanFeature.setFeature(featureService.findByIdOrElseThrow(salePlanFeatureDto.getFeatureId()));
        salePlanFeature.setSalePlan(salePlanService.findByIdOrElseThrow(salePlanFeatureDto.getSalePlanId()));
      //  salePlanFeature.setCount(salePlanFeatureDto.getCount());
        return save(salePlanFeature);
    }

    @Override
    public SalePlanFeatureDto mapToDto(SalePlanFeature salePlanFeature) {
        SalePlanFeatureDto salePlanFeatureDto = new SalePlanFeatureDto();
        salePlanFeatureDto.setFeatureId(salePlanFeature.getFeature().getId());
        salePlanFeatureDto.setFeatureName(salePlanFeature.getFeature().getName());
        salePlanFeatureDto.setSalePlanId(salePlanFeature.getSalePlan().getId());
        salePlanFeatureDto.setSalePlanName(salePlanFeature.getSalePlan().getName());
        salePlanFeatureDto.setId(salePlanFeature.getId());
        return salePlanFeatureDto;
    }

    @Override
    public void delete(Long id) {
        SalePlanFeature salePlanFeature = findByIdOrElseThrow(id);
        this.delete(salePlanFeature);
    }

    @Override
    public SearchDto list(SalePlanFeatureDto salePlanFeatureDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<SalePlanFeature>> optionalSpecification = createSpecification(salePlanFeatureDto);
        Page<SalePlanFeature> salePlanFeaturePage = findAll(optionalSpecification.get(), pageable);
        List<SalePlanFeatureDto> content = salePlanFeaturePage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(salePlanFeaturePage.getTotalElements())
                .totalPages(salePlanFeaturePage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public SalePlanFeature edit(SalePlanFeatureDto salePlanFeatureDto) {
        SalePlanFeature salePlanFeature = findByIdOrElseThrow(salePlanFeatureDto.getId());
       // salePlanFeature.setCount(salePlanFeatureDto.getCount());
        salePlanFeature.setFeature(featureService.findByIdOrElseThrow(salePlanFeatureDto.getFeatureId()));
        salePlanFeature.setSalePlan(salePlanService.findByIdOrElseThrow(salePlanFeatureDto.getSalePlanId()));
        return this.save(salePlanFeature);
    }
    @Override
    public List<SalePlanFeatureDto> findAllFeatureOfSalePlan(Long salePlanId) {

        SalePlanFeatureDto salePlanFeatureDto = new SalePlanFeatureDto();
        salePlanFeatureDto.setSalePlanId(salePlanId);
        SearchDto searchDto = this.list(salePlanFeatureDto, 0, PageUtils.MAX_PAGE_SIZE, null, null);
        return (List<SalePlanFeatureDto>) searchDto.getContent();
    }

//    @Override
//    public List<SalePlanFeatureDto> findAllFeatureOfSalePlanByFeatureCalcTypeList(Long salePlanId, List<String> featureCalcTypeList) {
//
//        SalePlanFeatureDto salePlanFeatureDto = new SalePlanFeatureDto();
//        salePlanFeatureDto.setSalePlanId(salePlanId);
//        salePlanFeatureDto.setFeatureCalcTypeList(featureCalcTypeList);
//        SearchDto searchDto = this.list(salePlanFeatureDto, 0, PageUtils.MAX_PAGE_SIZE, null, null);
//        return (List<SalePlanFeatureDto>) searchDto.getContent();
//    }

    private Optional<Specification<SalePlanFeature>> createSpecification(SalePlanFeatureDto salePlanFeatureDto) {
        Specification<SalePlanFeature> salePlanFeatures = (root, query, criteriaBuilder) -> {
            Join<SalePlanFeature, SalePlan> salePlanJoin = root.join(SalePlanFeature_.SALE_PLAN);
            Join<SalePlanFeature, Feature> featureJoin = root.join(SalePlanFeature_.FEATURE);
          //  Join<Feature, CodeCategory> codeCategoryJoin = featureJoin.join(Feature_.CALC_TYPE);
            List<Predicate> predicates = new ArrayList<>();

            if (salePlanFeatureDto.getSalePlanId() != null) {
                predicates.add(criteriaBuilder.equal(salePlanJoin.get(SalePlan_.ID), salePlanFeatureDto.getSalePlanId()));
            }
            if (salePlanFeatureDto.getFeatureId() != null) {
                predicates.add(
                        criteriaBuilder.equal(featureJoin.get(Feature_.ID), salePlanFeatureDto.getFeatureId()));
            }
            if (salePlanFeatureDto.getFeatureName() != null) {
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Feature_.NAME)),
                                "%" + salePlanFeatureDto.getFeatureName().toLowerCase() + "%"));
            }
            if (salePlanFeatureDto.getSalePlanName() != null) {
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(SalePlan_.NAME)),
                                "%" + salePlanFeatureDto.getFeatureName().toLowerCase() + "%"));
            }
//            if(!CollectionUtils.isEmpty(salePlanFeatureDto.getFeatureCalcTypeList())){
//                predicates.add(codeCategoryJoin.get(CodeCategory_.CODE).in(salePlanFeatureDto.getFeatureCalcTypeList()));
//            }
            if (predicates.isEmpty())
                return criteriaBuilder.and();
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return Optional.of(salePlanFeatures);
    }

}
