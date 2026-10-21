package com.hamsoo.bpm.service.impl.saleplan;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.DateUtils;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.saleplan.SalePlanDto;
import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.saleplan.SalePlanRepository;
import com.hamsoo.bpm.service.api.saleplan.SalePlanService;
import com.hamsoo.bpm.service.api.saleplanfeature.SalePlanFeatureService;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalePlanServiceImpl implements SalePlanService {
    private SalePlanRepository salePlanRepository;
    private SalePlanFeatureService salePlanFeatureService;

    public SalePlanServiceImpl(SalePlanRepository salePlanRepository,@Lazy SalePlanFeatureService salePlanFeatureService) {
        this.salePlanRepository = salePlanRepository;
        this.salePlanFeatureService = salePlanFeatureService;
    }


    @Override
    public SalePlanRepository getRepository() {
        return salePlanRepository;
    }

    @Override
    public SalePlan findByIdOrElseThrow(java.lang.Long id) {
        return getRepository().findByIdAndStatus(id,Status.ACTIVE.name()).orElseThrow(()->
         new BusinessException("error.slaPlan.notfound"));
    }
    @Override
    public Optional<SalePlan> findBySaleServiceTypeAndStatus(String serviceType,String status) {
        return getRepository().findBySaleServiceTypeAndStatus(serviceType,status);
    }

    @Override
    public SalePlanDto mapToDto(SalePlan salePlan) {
        SalePlanDto salePlanDto=new SalePlanDto();
        salePlanDto.setName(salePlan.getName());
        salePlanDto.setDescription(salePlan.getDescription());
        salePlanDto.setPlanVersion(salePlan.getPlanVersion());
        salePlanDto.setCode(salePlan.getCode());
        salePlanDto.setPrice(salePlan.getPrice());
        salePlanDto.setServiceType(salePlan.getSaleServiceType());
        List<SalePlanFeatureDto> salePlanFeatureDtos=salePlanFeatureService.findAllFeatureOfSalePlan(salePlan.getId());
        salePlanDto.setFeatureList(salePlanFeatureDtos);
        return salePlanDto;
    }

    @Override
    @Transactional
    public SalePlan create(SalePlanDto salePlanDto) {
        //must have one plan of each code
        Optional<SalePlan> findSalePlan=getRepository().findByCodeAndStatus(salePlanDto.getCode(),Status.ACTIVE.name());
        if(findSalePlan.isPresent()) passive(findSalePlan.get().getId());
        //must have on just one free and one selfHost and one EXPIRED salePlan and on free salePlan
        if(salePlanDto.getServiceTypes().equals(SalePlan.ServicesType.SELF_HOST.name()) ||
                salePlanDto.getServiceTypes().equals(SalePlan.ServicesType.FREE.name()) ||
                salePlanDto.getServiceTypes().equals(SalePlan.ServicesType.EXPIRED.name())
        ){
            Optional<SalePlan> curServiceTypeSalePlan=getRepository().findBySaleServiceTypeAndStatus(salePlanDto.getServiceType(),Status.ACTIVE.name());
            passive(curServiceTypeSalePlan.get().getId());
        }
        SalePlan salePlan = new SalePlan();
        salePlan.setName(salePlanDto.getName());
        salePlan.setDescription(salePlanDto.getDescription());
        salePlan.setSaleServiceType(salePlanDto.getServiceType());
        salePlan.setStatus(Status.ACTIVE.name());
        salePlan.setPrice(salePlanDto.getPrice());
        salePlan.setCode(salePlanDto.getCode());
        salePlan.setPlanVersion(DateUtils.currentDateTimePersianInString("YYYYMMDD", "en"));
        salePlan.setValidFrom(LocalDateTime.now());
        return save(salePlan);
    }

    @Override
    public SalePlan edit(SalePlanDto salePlanDto) {
        Assert.isNull(salePlanDto.getId(), "sale plan id is null");
        SalePlan salePlan = this.findByIdOrElseThrow(salePlanDto.getId());
        salePlan.setName(salePlanDto.getName());
        salePlan.setDescription(salePlanDto.getDescription());
        salePlan.setPrice(salePlanDto.getPrice());
        return save(salePlan);
    }

    @Override
    public void passive(java.lang.Long id) {
        Assert.isNull(id, "sale plan id is null");
        SalePlan salePlan = this.findByIdOrElseThrow(id);
        salePlan.setStatus(Status.PASSIVE.name());
        salePlan.setValidTo(LocalDateTime.now());
        save(salePlan);
    }

    @Override
    public SearchDto list(SalePlanDto salePlanDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<SalePlan>> optionalSpecification = createSpecification(salePlanDto);
        Page<SalePlan> salePlanPage = findAll(optionalSpecification.get(), pageable);
        List<SalePlanDto> content = salePlanPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(salePlanPage.getTotalElements())
                .totalPages(salePlanPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<SalePlan>> createSpecification(SalePlanDto salePlanDto) {
        Specification<SalePlan> activeSalePlan = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = criteriaBuilder.equal(root.get(SalePlan_.STATUS), Status.ACTIVE.name());
            if (salePlanDto.getName() != null) {
                predicates = ArrayUtils.addAll(predicates,criteriaBuilder.like(criteriaBuilder.lower(root.get(SalePlan_.NAME)),
                        "%" + salePlanDto.getName().toLowerCase() + "%"));
            }
            if (CollectionUtils.isEmpty(salePlanDto.getServiceTypes())) {
                predicates = ArrayUtils.addAll(predicates, root.get(SalePlan_.NAME).in(salePlanDto.getServiceTypes()));
            }
            return criteriaBuilder.and(predicates);
        };
        return Optional.of(activeSalePlan);
    }
}
