package com.hamsoo.bpm.service.impl.externalprocess;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.externalprocess.ExternalProcessDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess_;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.externalprocess.ExternalProcessRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExternalProcessServiceImpl implements ExternalProcessService {

    private final ExternalProcessRepository externalProcessRepository;
    private final OrganService organService;
    private final IoSupCustService ioSupCustService;
    private final ModelElementReferenceService modelElementReferenceService;

    public ExternalProcessServiceImpl(ExternalProcessRepository externalProcessRepository,
                                      OrganService organService,
                                      @Lazy IoSupCustService ioSupCustService,
                                      @Lazy ModelElementReferenceService modelElementReferenceService) {
        this.externalProcessRepository = externalProcessRepository;
        this.organService = organService;
        this.ioSupCustService = ioSupCustService;
        this.modelElementReferenceService = modelElementReferenceService;
    }

    @Override
    public ExternalProcessRepository getRepository() {
        return externalProcessRepository;
    }

    @Override
    public ExternalProcess findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.externalProcess.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.externalProcess.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ExternalProcess create(ExternalProcessDto externalProcessDto) {
        Assert.notNull(externalProcessDto, "externalProcessDto is null");
        ExternalProcess externalProcess = new ExternalProcess();
        externalProcess.setName(externalProcessDto.getName());
        externalProcess.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        externalProcess.setStatus(Status.ACTIVE.name());
        return save(externalProcess);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ExternalProcess edit(ExternalProcessDto externalProcessDto) {
        Assert.notNull(externalProcessDto, "externalProcessDto is null");
        ExternalProcess externalProcess = findByIdOrElseThrow(externalProcessDto.getId());
        externalProcess.setName(externalProcessDto.getName());
        return save(externalProcess);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        ExternalProcess externalProcess = findByIdOrElseThrow(id);
        Long count = ioSupCustService.countByExternalProcessId(id);
        if (count > 0) {
            throw new BusinessException("error.externalProcess.externalProcessUsedInIoSupCust");
        }
        Long modelElementReferenceCount = modelElementReferenceService
                .countByReferenceTypeCodeAndReferenceId(ModelElementRefrence.ReferenceType.EXTERNAL.getCode(), id);
        if (modelElementReferenceCount > 0) {
            throw new BusinessException("error.externalProcess.externalProcessReferenced");
        }
        externalProcess.setStatus(Status.PASSIVE.name());
        save(externalProcess);
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalProcessDto mapToDto(ExternalProcess externalProcess) {
        Assert.notNull(externalProcess, "externalProcess is null");
        externalProcess = findByIdOrElseThrow(externalProcess.getId());
        ExternalProcessDto externalProcessDto = new ExternalProcessDto();
        externalProcessDto.setId(externalProcess.getId());
        externalProcessDto.setName(externalProcess.getName());
        externalProcessDto.setOrganId(externalProcess.getOrgan().getId());
        return externalProcessDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ExternalProcessDto externalProcessDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ExternalProcess>> optionalSpecification = createSpecification(externalProcessDto);
        Page<ExternalProcess> externalProcessPage = findAll(optionalSpecification.get(), pageable);
        List<ExternalProcessDto> content = externalProcessPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(externalProcessPage.getTotalElements())
                .totalPages(externalProcessPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ExternalProcess>> createSpecification(ExternalProcessDto externalProcessDto) {
        List<Specification<ExternalProcess>> specificationList = new ArrayList<>();

        Specification<ExternalProcess> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ExternalProcess_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        Specification<ExternalProcess> organEq = (root, query, criteriaBuilder) -> {
            Join<ExternalProcess, Organ> organJoin = root.join(ExternalProcess_.ORGAN);
            return criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
        };
        specificationList.add(organEq);
        if (externalProcessDto.getFilterHasReference()) {
            specificationList.add(modelElementReferenceExist());
        }

        if (StringUtils.hasText(externalProcessDto.getName())) {
            Specification<ExternalProcess> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(ExternalProcess_.NAME), "%" + externalProcessDto.getName() + "%");
            specificationList.add(nameLike);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }


    private Specification<ExternalProcess> modelElementReferenceExist() {
        return (root, query, criteriaBuilder) -> {
            Subquery<ModelElementRefrence> subQuery = query.subquery(ModelElementRefrence.class);
            Root<ModelElementRefrence> subQueryRoot = subQuery.from(ModelElementRefrence.class);
            Join<ModelElementRefrence, CodeCategory> refTypJoin = subQueryRoot.join(ModelElementRefrence_.REFERENCE_TYPE);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(refTypJoin.get(CodeCategory_.CODE), ModelElementRefrence.ReferenceType.EXTERNAL.getCode());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(ModelElementRefrence_.REFERENCE_ID), root.get(ExternalProcess_.ID));
            subQuery.where(predicates);
            return criteriaBuilder.not(criteriaBuilder.exists(subQuery));
        };
    }


}
