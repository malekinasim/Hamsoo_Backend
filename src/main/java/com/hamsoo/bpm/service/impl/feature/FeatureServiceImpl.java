package com.hamsoo.bpm.service.impl.feature;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.feature.FeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.model.entity.feature.Feature_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.feature.FeatureRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.feature.FeatureService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeatureServiceImpl implements FeatureService {
    private FeatureRepository featureRepository;
    private CodeCategoryService codeCategoryService;

    public FeatureServiceImpl(FeatureRepository featureRepository, CodeCategoryService codeCategoryService) {
        this.featureRepository = featureRepository;
        this.codeCategoryService = codeCategoryService;
    }

    @Override
    public FeatureRepository getRepository() {
        return featureRepository;
    }

    @Override
    public Feature findByIdOrElseThrow(Long id) {
       return  this.getRepository().findByIdAndStatus(id,Status.ACTIVE.name()).orElseThrow(()->
                new BusinessException("error.feature.notFind")
        );

    }

    @Override
    public FeatureDto mapToDto(Feature feature) {

            FeatureDto featureDto=new FeatureDto();
            featureDto.setId(feature.getId());
            featureDto.setName(feature.getName());
            featureDto.setStatus(feature.getStatus());
//        featureDto.setCalcType(feature.getCalcType().getCode());
            return featureDto;
    }

    @Override
    public Feature create(FeatureDto featureDto) {
        Feature feature=new Feature();
        feature.setName(featureDto.getName());
        feature.setStatus(Status.ACTIVE.name());
        feature.setValidFrom(LocalDateTime.now());
//        feature.setCalcType(codeCategoryService.findByCategoryAndCode(Feature.CalcType.CATEGORY,featureDto.getCalcType()));
        return save(feature);
    }

    @Override
    public Feature edit(FeatureDto featureDto) {
        Assert.notNull(featureDto.getId(), "feature id is null");
       Feature feature= this.findByIdOrElseThrow(featureDto.getId());
        feature.setName(featureDto.getName());
//        feature.setCalcType(codeCategoryService.findByCategoryAndCode(Feature.CalcType.CATEGORY,featureDto.getCalcType()));
        return save(feature);
    }

    @Override
    public void passive(Long id) {
        Assert.notNull(id, "feature id is null");
        Feature feature= this.findByIdOrElseThrow(id);
        feature.setStatus(Status.PASSIVE.name());
        feature.setValidTo(LocalDateTime.now());
        save(feature);
    }

    @Override
    public SearchDto list(FeatureDto featureDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Feature>> optionalSpecification = createSpecification(featureDto);
        Page<Feature> featurePage = findAll(optionalSpecification.get(), pageable);
        List<FeatureDto> content = featurePage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(featurePage.getTotalElements())
                .totalPages(featurePage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public Optional<Feature> findByFeatureCalcType(String calcType) {

        return Optional.empty();
    }

    private Optional<Specification<Feature>> createSpecification(FeatureDto featureDto) {
        Specification<Feature> activeFeature = (root, query, criteriaBuilder) -> {
//           Join<Feature, CodeCategory> calcTypeJoin=root.join(Feature_.CALC_TYPE);
            Predicate[] predicates = new Predicate[1];

            predicates[0] = criteriaBuilder.equal(root.get(Feature_.STATUS), Status.ACTIVE.name());

            if (featureDto.getName() != null) {
                predicates = ArrayUtils.addAll(predicates,criteriaBuilder.like(criteriaBuilder.lower(root.get(Feature_.NAME)),
                        "%" + featureDto.getName().toLowerCase() + "%"));
            }
//            if (featureDto.getCalcTypeList() != null && !featureDto.getCalcTypeList().isEmpty()) {
//                predicates = ArrayUtils.addAll(predicates,
//                        calcTypeJoin.get(CodeCategory_.CODE).in( featureDto.getCalcTypeList()));
//            }
            return criteriaBuilder.and(predicates);
        };
        return Optional.of(activeFeature);
    }


}
