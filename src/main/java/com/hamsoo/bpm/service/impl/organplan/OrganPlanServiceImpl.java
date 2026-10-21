package com.hamsoo.bpm.service.impl.organplan;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan_;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.organplan.OrganPlanRepository;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.saleplan.SalePlanService;
import io.jsonwebtoken.lang.Assert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrganPlanServiceImpl implements OrganPlanService {
    private OrganPlanRepository organPlanRepository;
    private SalePlanService salePlanService;
    private OrganService organService;


    public OrganPlanServiceImpl(OrganPlanRepository organPlanRepository, SalePlanService salePlanService, OrganService organService) {
        this.organPlanRepository = organPlanRepository;
        this.salePlanService = salePlanService;
        this.organService = organService;
    }

    @Override
    public OrganPlanRepository getRepository() {
        return organPlanRepository;
    }

    @Override
    public OrganPlan findByIdOrElseThrow(Long id) {
        return getRepository().findByIdAndOrgan_IdAndValidToAfter(id, SecurityUtils.getCurrentOrganIdOrElseThrow(), LocalDateTime.now())
                .orElseThrow(() -> new BusinessException("error.organPlan.notfound"));
    }


    @Override
    public Optional<OrganPlan> getLastOrganSalePlan(Long OrganId) {
        return getRepository().findTopByOrgan_IdOrderByValidToDesc(OrganId);
    }

    @Override
    public OrganPlanDto mapToDto(OrganPlan organPlan) {
        OrganPlanDto organPlanDto = new OrganPlanDto();
        organPlanDto.setAmountPaid(organPlan.getAmountPaid());
        organPlanDto.setMaxCapacity(organPlan.getMaxCapacity());

        if(organPlan.getMaxCapacity().equals(-1)) organPlanDto.setHumanReadableMaxCapacity("-1");
        else organPlanDto.setHumanReadableMaxCapacity(humanReadableByteCountSI(organPlan.getMaxCapacity().longValue()));

        organPlanDto.setMaxModelCount(organPlan.getMaxModelCount());
        organPlanDto.setMaxUserCount(organPlan.getMaxUserCount());

        organPlanDto.setSalePlanId(organPlan.getSalePlan().getId());
        organPlanDto.setOrganId(organPlan.getOrgan().getId());
        organPlanDto.setSalePlanDto(salePlanService.mapToDto(organPlan.getSalePlan()));

        organPlanDto.setUsedCapacity(humanReadableByteCountSI(organService.getOrganModelCapacity(organPlan.getOrgan().getId()).longValue()));
        //maxCapacity of organModel
        if(organPlan.getMaxCapacity().equals(-1)) organPlanDto.setRemainCapacity("-1");
        else{
            BigInteger remainCapacity=organPlan.getMaxCapacity().add(organService.getOrganModelCapacity(organPlan.getOrgan().getId()).negate());
            
        organPlanDto.setRemainCapacity(humanReadableByteCountSI(remainCapacity.longValue()));}
        organPlanDto.setUsedUserCount(organService.getOrganUsersCount(organPlan.getOrgan().getId()));
        organPlanDto.setUsedModelCount(organService.getOrganModelCount(organPlan.getOrgan().getId()));

        if(!organPlan.getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.FREE.name())
                || !organPlan.getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.SELF_HOST.name()) ) {
            Period period = Period.between(organPlan.getValidFrom().toLocalDate(), LocalDate.now());
            Integer remainDay=organPlan.getSalePlan().getPlanDuration()-period.getDays();
            if(remainDay>0) {
                organPlanDto.setRemainingOrganPlanDay(remainDay);
            }else{
                organPlanDto.setRemainingOrganPlanDay(0);
            }
        }else{
            organPlanDto.setRemainingOrganPlanDay(-1);
        }
        organPlanDto.setValidFrom(organPlan.getValidFrom());
        organPlanDto.setValidTo(organPlan.getValidTo());
        return organPlanDto;
    }
    private  String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }





    @Override
    public OrganPlan create(OrganPlanDto organPlanDto) {
        Optional<OrganPlan> findOrganPlan = this.getRepository().findByOrgan_IdAndValidToAfter(SecurityUtils.getCurrentOrganIdOrElseThrow(), LocalDateTime.now());
        if (findOrganPlan.isPresent()) passive(findOrganPlan.get().getId());
        OrganPlan organPlan = new OrganPlan();
        organPlan.setAmountPaid(organPlanDto.getAmountPaid());
        organPlan.setMaxCapacity(organPlanDto.getMaxCapacity());
        organPlan.setMaxModelCount(organPlanDto.getMaxModelCount());
        organPlan.setMaxUserCount(organPlanDto.getMaxUserCount());
        organPlan.setOrgan(organService.findByIdOrElseThrow(organPlanDto.getOrganId()));

        SalePlan salePlan=salePlanService.findByIdOrElseThrow(organPlanDto.getSalePlanId());
        organPlan.setSalePlan(salePlan);
        organPlan.setValidFrom(LocalDateTime.now());
        //free sale plan have un_limit time
        if(!salePlan.getSaleServiceType().equals(SalePlan.ServicesType.FREE))
            organPlan.setValidTo(LocalDateTime.now().plus(Period.ofYears(1)));

        return save(organPlan);
    }

    @Override
    public OrganPlan edit(OrganPlanDto organPlanDto) {
        Assert.isNull(organPlanDto.getId(), "organ plan id is null");
        OrganPlan organPlan = this.findByIdOrElseThrow(organPlanDto.getId());
        organPlan.setAmountPaid(organPlanDto.getAmountPaid());
        organPlan.setMaxCapacity(organPlanDto.getMaxCapacity());
        organPlan.setMaxModelCount(organPlanDto.getMaxModelCount());
        organPlan.setMaxUserCount(organPlanDto.getMaxUserCount());
        return save(organPlan);
    }

    @Override
    public void passive(Long id) {
        Assert.isNull(id, "organ plan id is null");
        OrganPlan organPlan = this.findByIdOrElseThrow(id);
        organPlan.setValidTo(LocalDateTime.now());
        save(organPlan);
    }

    @Override
    public SearchDto list(OrganPlanDto organPlanDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<OrganPlan>> optionalSpecification = createSpecification(organPlanDto);
        Page<OrganPlan> organPlanPage = findAll(optionalSpecification.get(), pageable);
        List<OrganPlanDto> content = organPlanPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(organPlanPage.getTotalElements())
                .totalPages(organPlanPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<OrganPlan>> createSpecification(OrganPlanDto organPlanDto) {
        Specification<OrganPlan> OrganPlanFilter = (root, query, criteriaBuilder) -> {
            Join<OrganPlan, Organ> organPlanOrganJoin = root.join(OrganPlan_.ORGAN);
            Join<OrganPlan, SalePlan> salePlanJoin = root.join(OrganPlan_.SALE_PLAN);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(organPlanOrganJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            if (organPlanDto.getOrganId() != null) {
                predicates[1] = criteriaBuilder.equal(organPlanOrganJoin.get(Organ_.ID), organPlanDto.getOrganId());
            } else {
                predicates[1] = criteriaBuilder.equal(organPlanOrganJoin.get(Organ_.ID), SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            }
            if (organPlanDto.getSalePlanId() != null) {
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.equal(salePlanJoin.get(SalePlan_.ID), organPlanDto.getSalePlanId()));
            }
            return criteriaBuilder.and(predicates);
        };
        return Optional.of(OrganPlanFilter);
    }

   @Override
    public void CheckOrganUserCountLimitBaseOnLastOrganPlan(Long organId){
        Optional<OrganPlan> organPlan= this.getLastOrganSalePlan(organId);
        if(organPlan.isPresent() &&
                !organPlan.get().getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.SELF_HOST) &&
                !organPlan.get().getMaxUserCount().equals(-1)) {
            Integer userCount = organService.getOrganUsersCount(organId);
            if(organPlan.get().getMaxUserCount()<=userCount)
                throw new BusinessException("error.SalePlanUserCount.limit",organPlan.get().getSalePlan().getName(), organPlan.get().getMaxUserCount().toString(),
                        organPlan.get().getOrgan().getOrganType().equals(Organ.OrganType.PRIVATE.name())  ? "" :
                                organPlan.get().getOrgan().getDescription());
        }

    }

    @Override
    public void CheckOrganModelCountLimitBaseOnLastOrganPlan(Long organId) {
        Optional<OrganPlan> organPlan= this.getLastOrganSalePlan(organId);
        if(organPlan.isPresent() &&
                !organPlan.get().getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.SELF_HOST) &&
                !organPlan.get().getMaxModelCount().equals(-1)) {
            Integer modelCount = organService.getOrganModelCount(organId);
            if(organPlan.get().getMaxModelCount()<=modelCount)
                throw new BusinessException("error.SalePlanModelCount.limit",organPlan.get().getSalePlan().getName(),
                        organPlan.get().getMaxModelCount().toString(),
                        organPlan.get().getOrgan().getOrganType().equals(Organ.OrganType.PRIVATE.name())  ? "" :
                                organPlan.get().getOrgan().getDescription());
        }
    }


    @Override
    public void CheckOrganCapacityLimitBaseOnLastOrganPlan(Long organId) {
        Optional<OrganPlan> organPlan= this.getLastOrganSalePlan(organId);
        if(organPlan.isPresent() &&
                !organPlan.get().getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.SELF_HOST) &&
                organPlan.get().getMaxCapacity().intValue()!=-1
        ) {
            BigInteger modelDocCapacity = organService.getOrganModelCapacity(organId);

            if(organPlan.get().getMaxCapacity().compareTo(modelDocCapacity)<=0)
                throw new BusinessException("error.SalePlanCapacity.limit",organPlan.get().getSalePlan().getName(),
                 humanReadableByteCountSI(organPlan.get().getMaxCapacity().longValue()));
        }
    }
    @Scheduled(cron = "@midNight")//once a day at mid night (0 0 0 * * *)
    @SchedulerLock(name = "expiredOrganPlanLockedTask", lockAtMostFor = "30m", lockAtLeastFor = "20m")
   // @Transactional(rollbackFor = Exception.class)
    public void expiredOrganPlanBasedOnOrganPlanDayDuration() {
        List<OrganDto> organDtos=new ArrayList<>();
        SearchDto searchDto ;
        int page=0;
        do {
             searchDto = organService.list(new OrganDto(), page, PageUtils.MAX_PAGE_SIZE, null, Sort.Direction.ASC);
             organDtos.addAll((Collection<? extends OrganDto>) searchDto.getContent());
             page ++;
        }while (searchDto.getTotalPages()<page);

        organDtos.stream().map(OrganDto::getId).forEach(this::expiredCurOrganPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrganPlan expiredCurOrganPlan(Long organId){
       Optional<OrganPlan> organPlan=   this.getLastOrganSalePlan(organId);
        Optional<SalePlan> expireSalePlan=  salePlanService.findBySaleServiceTypeAndStatus(SalePlan.ServicesType.EXPIRED.name(),Status.ACTIVE.name());
       if(expireSalePlan.isEmpty()) throw new BusinessException("error.organPlan.ExpireSalePlanNotExist");
        if(organPlan.isPresent() && organPlan.get().getValidTo()!=null
       && LocalDateTime.now().isAfter(organPlan.get().getValidTo()) ){
           //expire OrganPlan
           //passive curOrgan
              this.passive(organPlan.get().getId());
              //add expire salePlan
               OrganPlanDto organPlanDto = new OrganPlanDto();
               organPlanDto.setAmountPaid(new BigInteger("0"));
               organPlanDto.setMaxCapacity(new BigInteger("0"));
               organPlanDto.setMaxModelCount(0);
               organPlanDto.setMaxUserCount(0);
               organPlanDto.setOrganId(organId);
               organPlanDto.setSalePlanId(expireSalePlan.get().getId());
              return this.create(organPlanDto);

       }
       return  null;
    }

}
