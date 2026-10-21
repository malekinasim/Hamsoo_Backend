package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.GeneralErrorException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.identity.IdentityIoDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.model.entity.identity.IdentityIo_;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog_;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IdentityIoRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.iocatalog.IoCatalogService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.util.Asserts;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class IdentityIoServiceImpl implements IdentityIoService {

    private final IdentityIoRepository identityIoRepository;
    private final ModelVersionService modelVersionService;
    private final CodeCategoryService codeCategoryService;
    private final IoSupCustService ioSupCustService;
    private final IoCatalogService ioCatalogService;

    public IdentityIoServiceImpl(IdentityIoRepository identityIoRepository,
                                 ModelVersionService modelVersionService,
                                 CodeCategoryService codeCategoryService,
                                 @Lazy IoSupCustService ioSupCustService,
                                 @Lazy IoCatalogService ioCatalogService) {
        this.identityIoRepository = identityIoRepository;
        this.modelVersionService = modelVersionService;
        this.codeCategoryService = codeCategoryService;
        this.ioSupCustService = ioSupCustService;
        this.ioCatalogService = ioCatalogService;
    }

    @Override
    public IdentityIoRepository getRepository() {
        return identityIoRepository;
    }

    @Override
    public IdentityIo findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.identityIo.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.identityIo.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IdentityIo create(IdentityIoDto identityIoDto) {
        Assert.notNull(identityIoDto, "identityIoDto is null");
        modelVersionService.checkModelVersionStatus(identityIoDto.getModelVersionId());
        IdentityIo identityIo = new IdentityIo();
        identityIo.setType(codeCategoryService.findByCategoryAndCode(IdentityIo.Type.CATEGORY, identityIoDto.getTypeCode()));
        identityIo.setIoCatalog(ioCatalogService.findByIdOrElseThrow(identityIoDto.getIoCatalogId()));
        identityIo.setStatus(Status.ACTIVE.name());
        identityIo.setModelVersion(modelVersionService.findByIdOrElseThrow(identityIoDto.getModelVersionId()));
        validateIdentityIoDuplication(identityIoDto);
        return save(identityIo);
    }

    private void validateIdentityIoDuplication(IdentityIoDto identityIoDto) {
        Optional<IdentityIo> identityIoOptional = findEqualActiveIdentityIo(identityIoDto);
        if (identityIoOptional.isPresent()) {
            throw new BusinessException("error.identityIo.identityIoIsDuplicate");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdentityIo> findEqualActiveIdentityIo(IdentityIoDto identityIoDto) {
        return getRepository().findEqualActiveIdentityIo(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                identityIoDto.getTypeCode(),
                identityIoDto.getIoCatalogId(),
                identityIoDto.getModelVersionId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IdentityIo identityIo = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(identityIo.getModelVersion().getId());
        Optional<ErrorDto> optionalErrorDto = validateIdentityIoCanPassive(id);
        if (optionalErrorDto.isPresent()) {
            throw new GeneralErrorException(Collections.singletonList(optionalErrorDto.get()));
        }
        identityIo.setStatus(Status.PASSIVE.name());
        save(identityIo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ErrorDto> validateIdentityIoCanPassive(Long id) {
        IdentityIo identityIo = findByIdOrElseThrow(id);
        Optional<ErrorDto> result = Optional.empty();
        Long count = ioSupCustService.countByIdentityIoId(id, null, null);
        if (count > 0) {
            String errorMessage = ResourceBundleUtils.getMessage("error.identityIo.identityIoHasIoSupCust",
                    identityIo.getType().getFaName(), identityIo.getIoCatalog().getName());
            ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR)
                    .message(errorMessage).build();
            result = Optional.of(errorDto);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityIoDto mapToDto(IdentityIo identityIo) {
        Assert.notNull(identityIo, "identityIo is null");
        identityIo = findByIdOrElseThrow(identityIo.getId());
        IdentityIoDto identityIoDto = new IdentityIoDto();
        identityIoDto.setId(identityIo.getId());
        identityIoDto.setTypeCode(identityIo.getType().getCode());
        identityIoDto.setTypeTitle(identityIo.getType().getFaName());
        identityIoDto.setIoCatalogDto(ioCatalogService.mapToDto(identityIo.getIoCatalog()));
        identityIoDto.setModelVersionId(identityIo.getModelVersion().getId());
        return identityIoDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityIoDto> mapToDto(List<IdentityIo> identityIoList) {
        if (CollectionUtils.isEmpty(identityIoList)) {
            return new ArrayList<>();
        }
        return identityIoList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityIoDto mapToDtoWithAdditionalData(IdentityIo identityIo) {
        Assert.notNull(identityIo, "identityIo is null");
        IdentityIoDto identityIoDto = mapToDto(identityIo);
        Long supCustCount = ioSupCustService.countByIdentityIoId(identityIo.getId(), null, null);
        identityIoDto.setSupCustCount(supCustCount);
        List<ErrorDto> errorDtoList = validateIdentityIo(identityIo);
        if (!CollectionUtils.isEmpty(errorDtoList)) {
            identityIoDto.setErrorDtoList(errorDtoList);
        }
        return identityIoDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorDto> validateIdentityIo(IdentityIo identityIo) {
        Assert.notNull(identityIo, "identityIo is null");
        List<ErrorDto> errorDtoList = new ArrayList<>();
        identityIo = findByIdOrElseThrow(identityIo.getId());
        Long supCustCount = ioSupCustService.countByIdentityIoId(identityIo.getId(), null, null);
        if (supCustCount == 0) {
            String errorMessage = ResourceBundleUtils.getMessage("error.identityIo.identityIoMustHaveIoSupCust",
                    identityIo.getType().getFaName(), identityIo.getIoCatalog().getName());
            ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR).message(errorMessage).build();
            errorDtoList.add(errorDto);
        }
        return errorDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorDto> validateIdentityIo(List<IdentityIo> identityIoList) {
        if (CollectionUtils.isEmpty(identityIoList)) {
            return Collections.emptyList();
        }
        List<ErrorDto> result = new ArrayList<>();
        identityIoList.forEach(identityIo -> {
            List<ErrorDto> errorDtoList = validateIdentityIo(identityIo);
            if (!CollectionUtils.isEmpty(errorDtoList)) {
                result.addAll(errorDtoList);
            }
        });
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IdentityIoDto identityIoDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<IdentityIo, IdentityIoDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IdentityIo>> optionalSpecification = createSpecification(identityIoDto);
        Page<IdentityIo> identityIoPage = findAll(optionalSpecification.get(), pageable);
        List<IdentityIoDto> content = identityIoPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(identityIoPage.getTotalElements())
                .totalPages(identityIoPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IdentityIo>> createSpecification(IdentityIoDto identityIoDto) {
        List<Specification<IdentityIo>> specificationList = new ArrayList<>();

        Specification<IdentityIo> organEq = (root, query, criteriaBuilder) -> {
            Join<IdentityIo, ModelVersion> modelVersionJoin = root.join(IdentityIo_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), identityIoDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(root.get(IdentityIo_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            if (identityIoDto.getIoCatalogId() != null) {
                Join<IdentityIo, IoCatalog> ioCatalogJoin = root.join(IdentityIo_.IO_CATALOG);
                Predicate ioCatalogEq = criteriaBuilder.equal(ioCatalogJoin.get(IoCatalog_.ID), identityIoDto.getIoCatalogId());
                predicates = ArrayUtils.addAll(predicates, ioCatalogEq);
            }
            if (StringUtils.hasText(identityIoDto.getTypeCode())) {
                Join<IdentityIo, CodeCategory> typeJoin = root.join(IdentityIo_.TYPE);
                Predicate typeEq = criteriaBuilder.equal(typeJoin.get(CodeCategory_.CODE), identityIoDto.getTypeCode());
                predicates = ArrayUtils.addAll(predicates, typeEq);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityIo> findActiveByModelVersionId(Long modelVersionId) {
        return getRepository().findActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(), modelVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityIo clone(IdentityIo identityIo) {
        Asserts.notNull(identityIo, "identityIo is null");
        identityIo = findByIdOrElseThrow(identityIo.getId());
        IdentityIo clone = new IdentityIo();
        clone.setType(identityIo.getType());
        clone.setIoCatalog(identityIo.getIoCatalog());
        clone.setStatus(identityIo.getStatus());
        clone.setModelVersion(identityIo.getModelVersion());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByIoCatalogId(Long ioCatalogId) {
        return getRepository().countByIoCatalogId(ioCatalogId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityIo> findInternalActiveByModelVersionId(String typeCode, Long modelVersionId) {
        return getRepository().findAllInternalActiveByModelVersionId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                typeCode, modelVersionId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void copyIoIdentityAndIoSupCust(Long modelVersionIdFrom, Long modelVersionIdTo) {
        List<IdentityIo> identityIoList = findActiveByModelVersionId(modelVersionIdFrom);
        if (CollectionUtils.isEmpty(identityIoList)) {
            return;
        }
        ModelVersion modelVersionTo = modelVersionService.findByIdOrElseThrow(modelVersionIdTo);
        identityIoList.forEach(identityIo -> {
            IdentityIo clonedIdentityIo = clone(identityIo);
            clonedIdentityIo.setModelVersion(modelVersionTo);
            IdentityIo newIdentityIo = save(clonedIdentityIo);
            List<IoSupCust> ioSupCustList = ioSupCustService.findByIdentityIoId(identityIo.getId(), null, null);
            if (!CollectionUtils.isEmpty(ioSupCustList)) {
                ioSupCustList.forEach(ioSupCust -> {
                    IoSupCust clonedIoSupCust = ioSupCustService.clone(ioSupCust);
                    clonedIoSupCust.setIdentityIo(newIdentityIo);
                    if (clonedIoSupCust.getType().getCode().equals(IoSupCust.Type.INTERNAL_PROCESS.getCode())) {
                        ModelVersion lastVersion = modelVersionService.getLastVersion(clonedIoSupCust.getInternalSupCust().getId());
                        clonedIoSupCust.setInternalSupCust(lastVersion);
                    }
                    ioSupCustService.save(clonedIoSupCust);
                });
            }
        });
    }
}
