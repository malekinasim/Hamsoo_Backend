package com.hamsoo.bpm.service.impl.identity;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.GeneralErrorException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.identity.IoSupCustDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.model.entity.identity.IdentityIo_;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.identity.IoSupCust_;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.identity.IoSupCustRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.externalprocess.ExternalProcessService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.util.Asserts;
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
import java.util.*;
import java.util.function.Function;

@Service
public class IoSupCustServiceImpl implements IoSupCustService {

    private final IoSupCustRepository ioSupCustRepository;
    private final CodeCategoryService codeCategoryService;
    private final ModelVersionService modelVersionService;
    private final ExternalProcessService externalProcessService;
    private final IdentityIoService identityIoService;

    public IoSupCustServiceImpl(IoSupCustRepository ioSupCustRepository,
                                CodeCategoryService codeCategoryService,
                                ModelVersionService modelVersionService,
                                ExternalProcessService externalProcessService,
                                IdentityIoService identityIoService) {
        this.ioSupCustRepository = ioSupCustRepository;
        this.codeCategoryService = codeCategoryService;
        this.modelVersionService = modelVersionService;
        this.externalProcessService = externalProcessService;
        this.identityIoService = identityIoService;
    }

    @Override
    public IoSupCustRepository getRepository() {
        return ioSupCustRepository;
    }

    @Override
    public IoSupCust findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.ioSupCust.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.ioSupCust.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IoSupCust create(IoSupCustDto ioSupCustDto) {
        Assert.notNull(ioSupCustDto, "ioSupCustDto is null");
        IdentityIo identityIo= identityIoService.findByIdOrElseThrow(ioSupCustDto.getIdentityIoId());
        modelVersionService.checkModelVersionStatus(identityIo.getModelVersion().getId());
        IoSupCust ioSupCust = new IoSupCust();
        ioSupCust.setType(codeCategoryService.findByCategoryAndCode(IoSupCust.Type.CATEGORY, ioSupCustDto.getTypeCode()));
        if (IoSupCust.Type.INTERNAL_PROCESS.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCust.setInternalSupCust(modelVersionService.findByIdOrElseThrow(ioSupCustDto.getInternalSupCustId()));

        } else if (IoSupCust.Type.EXTERNAL_PROCESS.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCust.setExternalSupCust(externalProcessService.findByIdOrElseThrow(ioSupCustDto.getExternalSupCustId()));

        } else if (IoSupCust.Type.EVENT.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCust.setEventSupCust(ioSupCustDto.getEventSupCust());
        }
        ioSupCust.setStatus(Status.ACTIVE.name());
        ioSupCust.setIdentityIo(identityIo);
        validateIoSupCustDuplication(ioSupCustDto);
        return save(ioSupCust);
    }

    private void validateIoSupCustDuplication(IoSupCustDto ioSupCustDto) {
        Optional<IoSupCust> identityIoOptional = findEqualActiveIoSupCust(ioSupCustDto);
        if (identityIoOptional.isPresent()) {
            throw new BusinessException("error.ioSupCust.ioSupCustIsDuplicate");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IoSupCust> findEqualActiveIoSupCust(IoSupCustDto ioSupCustDto) {
        return getRepository().findEqualActiveIoSupCust(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                ioSupCustDto.getTypeCode(),
                ioSupCustDto.getIdentityIoId(),
                ioSupCustDto.getInternalSupCustId(),
                ioSupCustDto.getExternalSupCustId(),
                ioSupCustDto.getEventSupCust());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IoSupCust ioSupCust = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(ioSupCust.getIdentityIo().getModelVersion().getId());

        Optional<ErrorDto> optionalErrorDto = validateIoSupCustCanPassive(id);
        if (optionalErrorDto.isPresent()) {
            throw new GeneralErrorException(Collections.singletonList(optionalErrorDto.get()));
        }
        ioSupCust.setStatus(Status.PASSIVE.name());
        save(ioSupCust);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ErrorDto> validateIoSupCustCanPassive(Long id) {
        IoSupCust ioSupCust = findByIdOrElseThrow(id);
        Optional<ErrorDto> result = Optional.empty();
        if (!ioSupCust.getType().getCode().equals(IoSupCust.Type.INTERNAL_PROCESS.getCode())) {
            return result;
        }
        //define variable
        IdentityIo identityIo = ioSupCust.getIdentityIo();
        String identityIoTypeCode = identityIo.getType().getCode();
        IoCatalog ioCatalog = identityIo.getIoCatalog();
        ModelVersion internalSupCust = ioSupCust.getInternalSupCust();
        // check supplier/customer(internalSupCustId) status is approved or for approve.
        List<String> modelVersionStatusCodeList = Arrays.asList(ModelVersion.Status.APPROVE.getCode(),
                ModelVersion.Status.FOR_APPROVE.getCode());
        // internalSupCustId has output of ioCatalogId with modelVersionId customer or
        // internalSupCustId has input of ioCatalogId with modelVersionId supplier.
        String identityIoTypeCodeOfOtherSide = identityIoTypeCode.equals(IdentityIo.Type.INPUT.getCode()) ?
                IdentityIo.Type.OUTPUT.getCode() : IdentityIo.Type.INPUT.getCode();
        Optional<IoSupCust> optionalIoSupCust = findActiveInternalIoSupCust(internalSupCust.getId(), identityIoTypeCodeOfOtherSide,
                ioCatalog.getId(), identityIo.getModelVersion().getId(), modelVersionStatusCodeList);
        if (optionalIoSupCust.isPresent()) {
            String errorMessageCode = identityIoTypeCode.equals(IdentityIo.Type.INPUT.getCode()) ?
                    "error.ioSupCust.internalSupIsApprovedModel" : "error.ioSupCust.internalCustIsApprovedModel";
            String errorMessage = ResourceBundleUtils.getMessage(errorMessageCode,
                    internalSupCust.getModel().getName(), ioCatalog.getName());
            ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR).message(errorMessage).build();
            result = Optional.of(errorDto);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IoSupCustDto mapToDto(IoSupCust ioSupCust) {
        Assert.notNull(ioSupCust, "ioSupCust is null");
        ioSupCust = findByIdOrElseThrow(ioSupCust.getId());
        IoSupCustDto ioSupCustDto = new IoSupCustDto();
        ioSupCustDto.setId(ioSupCust.getId());
        ioSupCustDto.setTypeCode(ioSupCust.getType().getCode());
        ioSupCustDto.setTypeTitle(ioSupCust.getType().getFaName());
        if (IoSupCust.Type.INTERNAL_PROCESS.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCustDto.setInternalSupCust(modelVersionService.mapToDto(ioSupCust.getInternalSupCust()));
        } else if (IoSupCust.Type.EXTERNAL_PROCESS.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCustDto.setExternalSupCust(externalProcessService.mapToDto(ioSupCust.getExternalSupCust()));
        } else if (IoSupCust.Type.EVENT.getCode().equals(ioSupCustDto.getTypeCode())) {
            ioSupCustDto.setEventSupCust(ioSupCust.getEventSupCust());
        }
        ioSupCustDto.setIdentityIoId(ioSupCust.getIdentityIo().getId());
        return ioSupCustDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IoSupCustDto> mapToDto(List<IoSupCust> ioSupCustList) {
        if (CollectionUtils.isEmpty(ioSupCustList)) {
            return new ArrayList<>();
        }
        return ioSupCustList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IoSupCustDto mapToDtoWithAdditionalData(IoSupCust ioSupCust) {
        Assert.notNull(ioSupCust, "ioSupCust is null");
        IoSupCustDto ioSupCustDto = mapToDto(ioSupCust);
        List<ErrorDto> errorDtoList = validateIoSupCust(ioSupCust);
        if (!CollectionUtils.isEmpty(errorDtoList)) {
            ioSupCustDto.setErrorDtoList(errorDtoList);
        }
        return ioSupCustDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IoSupCustDto ioSupCustDto, int page, int size, String orderBy, Sort.Direction direction,
                          Function<IoSupCust, IoSupCustDto> mapper) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IoSupCust>> optionalSpecification = createSpecification(ioSupCustDto);
        Page<IoSupCust> ioSupCustPage = findAll(optionalSpecification.get(), pageable);
        List<IoSupCustDto> content = ioSupCustPage.map(mapper).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(ioSupCustPage.getTotalElements())
                .totalPages(ioSupCustPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IoSupCust>> createSpecification(IoSupCustDto ioSupCustDto) {
        List<Specification<IoSupCust>> specificationList = new ArrayList<>();

        Specification<IoSupCust> organEq = (root, query, criteriaBuilder) -> {
            Join<IoSupCust, IdentityIo> identityIoJoin = root.join(IoSupCust_.IDENTITY_IO);
            Join<IdentityIo, ModelVersion> modelVersionJoin = identityIoJoin.join(IdentityIo_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Predicate[] predicates = new Predicate[5];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(identityIoJoin.get(IdentityIo_.ID), ioSupCustDto.getIdentityIoId());
            predicates[2] = criteriaBuilder.equal(root.get(IoSupCust_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(identityIoJoin.get(IdentityIo_.STATUS), Status.ACTIVE.name());
            if (StringUtils.hasText(ioSupCustDto.getTypeCode())) {
                Join<IoSupCust, CodeCategory> typeJoin = root.join(IoSupCust_.TYPE);
                Predicate typeEq = criteriaBuilder.equal(typeJoin.get(CodeCategory_.CODE), ioSupCustDto.getTypeCode());
                predicates = ArrayUtils.addAll(predicates, typeEq);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    public List<String> getInternalProcessAsisTobeCodeList(ModelVersion modelVersion) {

        List<String> asisTobeCodeList;
        if (modelVersion.getAsisTobe().getCode().equals(ModelVersion.AsisTobe.AS_IS.getCode())) {
            asisTobeCodeList = Collections.singletonList(ModelVersion.AsisTobe.AS_IS.getCode());
        } else {
            asisTobeCodeList = Arrays.asList(ModelVersion.AsisTobe.AS_IS.getCode(), ModelVersion.AsisTobe.TO_BE.getCode());
        }
        return asisTobeCodeList;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto getInternalProcessList(Long modelId, IoSupCustDto ioSupCustDto, List<String> asisTobeCodeList, String modelName, int page, int size) {


        List<Long> lastModelVersionIdList = modelVersionService.getLastVersions(modelId)
                .stream().map(ModelVersion::getId).toList();
        Page<ModelVersion> modelVersionPage = modelVersionService.getInternalProcessList(asisTobeCodeList, lastModelVersionIdList,
                SecurityUtils.getCurrentOrganIdOrElseThrow(), modelName, ioSupCustDto.getModelTypeCodeList(), ioSupCustDto.getFilterHasReference(),
                PageUtils.getInstance(page, size));

        List<ModelVersionDto> content = modelVersionPage.map(this::mapToDtoForInputList).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelVersionPage.getTotalElements())
                .totalPages(modelVersionPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private ModelVersionDto mapToDtoForInputList(ModelVersion modelVersion) {
        Assert.notNull(modelVersion, "modelVersion is null");
        modelVersion = modelVersionService.findByIdOrElseThrow(modelVersion.getId());
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setId(modelVersion.getId());
        modelVersionDto.setAsisTobeCode(modelVersion.getAsisTobe().getCode());
        modelVersionDto.setAsisTobeTitle(modelVersion.getAsisTobe().getFaName());
        modelVersionDto.setModelName(modelVersion.getModel().getName());
        modelVersionDto.setModelNameWithAsisTobe(modelVersion.getModel().getName() + '(' + modelVersion.getAsisTobe().getFaName() + ')');
        modelVersionDto.setModelId(modelVersion.getModel().getId());
        return modelVersionDto;
    }

    /**
     * @param identityIoTypeCode    is optional(can be null)
     * @param ioSupCustTypeCodeList is optional(can be null)
     */
    @Override
    @Transactional(readOnly = true)
    public List<IoSupCust> findByIdentityIoId(Long identityIoId, String identityIoTypeCode, List<String> ioSupCustTypeCodeList) {
        return getRepository().findByIdentityIoId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                identityIoId,
                identityIoTypeCode,
                ioSupCustTypeCodeList);
    }

    /**
     * @param identityIoTypeCode    is optional(can be null)
     * @param ioSupCustTypeCodeList is optional(can be null)
     */
    @Override
    @Transactional(readOnly = true)
    public Long countByIdentityIoId(Long identityIoId, String identityIoTypeCode, List<String> ioSupCustTypeCodeList) {
        return getRepository().countByIdentityIoId(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                identityIoId,
                identityIoTypeCode,
                ioSupCustTypeCodeList);
    }

    /**
     * @param modelVersionStatusCodeList is optional(can be null)
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IoSupCust> findActiveInternalIoSupCust(Long modelVersionId,
                                                           String identityIoTypeCode,
                                                           Long ioCatalogId,
                                                           Long internalSupCustId,
                                                           List<String> modelVersionStatusCodeList) {
        return getRepository().findActiveInternalIoSupCust(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                modelVersionId,
                identityIoTypeCode,
                ioCatalogId,
                IoSupCust.Type.INTERNAL_PROCESS.getCode(),
                internalSupCustId,
                modelVersionStatusCodeList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IoSupCust> findActiveInternalIoSupCust(Long modelVersionId) {
        return getRepository().findActiveInternalIoSupCust(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                modelVersionId,
                IoSupCust.Type.INTERNAL_PROCESS.getCode());
    }

    /**
     * @param newModelVersion model version with future identity
     * @param oldModelVersion model version with current identity
     */
    @Override
    @Transactional(readOnly = true)
    public void checkUpdateInternalSupCust(ModelVersion newModelVersion, ModelVersion oldModelVersion) {
        Assert.notNull(newModelVersion, "newModelVersion is null");
        Assert.notNull(oldModelVersion, "oldModelVersion is null");
        //ignore input or output without supplier or customer
        List<IoSupCust> ioSupCustList = findActiveInternalIoSupCust(oldModelVersion.getId());
        if (CollectionUtils.isEmpty(ioSupCustList)) {
            return;
        }
        List<ErrorDto> errorDtoList = ioSupCustList.stream().map(ioSupCust -> {
            Optional<ErrorDto> optionalErrorDto = Optional.empty();
            String identityIoTypeCode = ioSupCust.getIdentityIo().getType().getCode();
            Long ioCatalogId = ioSupCust.getIdentityIo().getIoCatalog().getId();
            Long internalSupCustId = ioSupCust.getInternalSupCust().getId();
            Optional<IoSupCust> ioSupCustOptional = findActiveInternalIoSupCust(newModelVersion.getId(),
                    identityIoTypeCode, ioCatalogId, internalSupCustId, null);
            if (ioSupCustOptional.isEmpty()) {
                optionalErrorDto = validateIoSupCustCanPassive(ioSupCust.getId());
            }
            return optionalErrorDto;
        }).flatMap(Optional::stream).toList();
        if (!CollectionUtils.isEmpty(errorDtoList)) {
            throw new GeneralErrorException(errorDtoList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateInternalSupCust(Long newSupCustId, Long oldSupCustId) {
        Assert.notNull(newSupCustId, "newSupCustId is null");
        Assert.notNull(oldSupCustId, "oldSupCustId is null");
        getRepository().updateInternalSupCust(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                IoSupCust.Type.INTERNAL_PROCESS.getCode(),
                newSupCustId,
                oldSupCustId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorDto> validateIoSupCust(IoSupCust ioSupCust) {
        Assert.notNull(ioSupCust, "ioSupCust is null");
        List<ErrorDto> errorDtoList = new ArrayList<>();
        ioSupCust = findByIdOrElseThrow(ioSupCust.getId());
        if (ioSupCust.getType().getCode().equals(IoSupCust.Type.INTERNAL_PROCESS.getCode())) {
            //define variable
            IdentityIo identityIo = ioSupCust.getIdentityIo();
            String identityIoTypeCode = identityIo.getType().getCode();
            IoCatalog ioCatalog = identityIo.getIoCatalog();
            ModelVersion internalSupCust = ioSupCust.getInternalSupCust();
            // internalSupCustId has output of ioCatalogId with modelVersionId customer or
            // internalSupCustId has input of ioCatalogId with modelVersionId supplier.
            String identityIoTypeCodeOfOtherSide = identityIoTypeCode.equals(IdentityIo.Type.INPUT.getCode()) ?
                    IdentityIo.Type.OUTPUT.getCode() : IdentityIo.Type.INPUT.getCode();
            Optional<IoSupCust> optionalIoSupCust = findActiveInternalIoSupCust(internalSupCust.getId(), identityIoTypeCodeOfOtherSide,
                    ioCatalog.getId(), identityIo.getModelVersion().getId(), null);
            if (optionalIoSupCust.isEmpty()) {
                String errorMessageCode = identityIoTypeCode.equals(IdentityIo.Type.INPUT.getCode()) ?
                        "error.ioSupCust.internalSupIsNotValid" : "error.ioSupCust.internalCustIsNotValid";
                String errorMessage = ResourceBundleUtils.getMessage(errorMessageCode,
                        internalSupCust.getModel().getName(), ioCatalog.getName());
                ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR).message(errorMessage).build();
                errorDtoList.add(errorDto);
            }
        }
        return errorDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorDto> validateIoSupCust(List<IoSupCust> ioSupCustList) {
        if (CollectionUtils.isEmpty(ioSupCustList)) {
            return Collections.emptyList();
        }
        List<ErrorDto> result = new ArrayList<>();
        ioSupCustList.forEach(ioSupCust -> {
            List<ErrorDto> errorDtoList = validateIoSupCust(ioSupCust);
            if (!CollectionUtils.isEmpty(errorDtoList)) {
                result.addAll(errorDtoList);
            }
        });
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IoSupCust clone(IoSupCust ioSupCust) {
        Asserts.notNull(ioSupCust, "ioSupCust is null");
        ioSupCust = findByIdOrElseThrow(ioSupCust.getId());
        IoSupCust clone = new IoSupCust();
        clone.setType(ioSupCust.getType());
        clone.setInternalSupCust(ioSupCust.getInternalSupCust());
        clone.setExternalSupCust(ioSupCust.getExternalSupCust());
        clone.setEventSupCust(ioSupCust.getEventSupCust());
        clone.setStatus(ioSupCust.getStatus());
        clone.setIdentityIo(ioSupCust.getIdentityIo());
        return clone;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByExternalProcessId(Long externalProcessId) {
        return getRepository().countByExternalProcessId(IoSupCust.Type.EXTERNAL_PROCESS.getCode(), externalProcessId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countInternalSupCustByModelId(Long modelId) {
        return getRepository().countInternalSupCustByModelId(modelId, IoSupCust.Type.INTERNAL_PROCESS.getCode());
    }


    @Override
    @Transactional(readOnly = true)
    public List<IoSupCust> findAllIoCatalogActiveInternalIoSupCust(Long modelVersionId,
                                                                   Long ioCatalogId) {
        return getRepository().findAllIoCatalogActiveInternalIoSupCust(SecurityUtils.getCurrentOrganIdOrElseThrow(),
                modelVersionId,
                ioCatalogId,
                IoSupCust.Type.INTERNAL_PROCESS.getCode());
    }
}
