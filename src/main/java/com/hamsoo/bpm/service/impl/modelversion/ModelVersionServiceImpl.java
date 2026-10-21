package com.hamsoo.bpm.service.impl.modelversion;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.*;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelversion.ModelVersionRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelVersionServiceImpl implements ModelVersionService {

    private final ModelVersionRepository modelVersionRepository;
    private final CodeCategoryService codeCategoryService;
    private final FilesService filesService;
    private final ReportGenerator reportGenerator;
    private final UserService userService;
    private final IoSupCustService ioSupCustService;
    private final ModelService modelService;
    private final IdentityIoService identityIoService;

    private final ModelElementReferenceService modelElementReferenceService;

    public ModelVersionServiceImpl(ModelVersionRepository modelVersionRepository, CodeCategoryService codeCategoryService, FilesService filesService, ReportGenerator reportGenerator, UserService userService, @Lazy IoSupCustService ioSupCustService, @Lazy ModelService modelService, @Lazy IdentityIoService identityIoService, @Lazy ModelElementReferenceService modelElementReferenceService) {
        this.modelVersionRepository = modelVersionRepository;
        this.codeCategoryService = codeCategoryService;
        this.filesService = filesService;
        this.reportGenerator = reportGenerator;
        this.userService = userService;
        this.ioSupCustService = ioSupCustService;
        this.modelService = modelService;
        this.identityIoService = identityIoService;
        this.modelElementReferenceService = modelElementReferenceService;
    }

    @Override
    public ModelVersionRepository getRepository() {
        return modelVersionRepository;
    }

    @Override
    public ModelVersion findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelversion.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.modelversion.idIsNotValid"));
    }

    @Override
    public ModelVersion findLockedByIdAndOrganId(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelversion.idIsNotValid");
        }
        return getRepository().findLockedByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(() -> new BusinessException("error.modelversion.idIsNotValid"));
    }

    @Override
    public void deleteModelVersion(Long id) {
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        delete(modelVersion);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion create(ModelVersionDto modelVersionDto) {
        Assert.notNull(modelVersionDto, "modelVersionDto is null");
        ModelVersion modelVersion = new ModelVersion();
        modelVersion.setVersionNum(modelVersionDto.getVersionNum());
        modelVersion.setValidFrom(modelVersionDto.getValidFrom());
        modelVersion.setValidTo(modelVersionDto.getValidTo());
        modelVersion.setAsisTobe(codeCategoryService.findByCategoryAndCode(ModelVersion.AsisTobe.CATEGORY, modelVersionDto.getAsisTobeCode()));
        modelVersion.setStatus(codeCategoryService.findByCategoryAndCode(ModelVersion.Status.CATEGORY, modelVersionDto.getStatusCode()));
        modelVersion.setModel(modelService.findByIdOrElseThrow(modelVersionDto.getModelId()));
        modelVersion.setLastVersion(modelVersionDto.getLastVersion());
        if (modelVersionDto.getXml() != null) {
            modelVersion.setXml(filesService.create(modelVersionDto.getXml()));
        }
        if (modelVersionDto.getImage() != null) {
            modelVersion.setImage(filesService.create(modelVersionDto.getImage()));
        }
        //modelVersion.setResourceSchemaList(modelVersionDto.getResourceSchemaList());
        modelVersion.setIdentityDataSchema(modelVersionDto.getIdentityDataSchema());
        modelVersion = save(modelVersion);
        if (modelVersionDto.getParentId() == null) {
            modelVersion.setParent(modelVersion);
        } else {
            modelVersion.setParent(findByIdOrElseThrow(modelVersionDto.getParentId()));
        }
        modelVersion.setMilestoneName(modelVersionDto.getMilestoneName());
        return modelVersion;
    }

    @Override
    public ModelVersion renameMilestoneName(Long id, ModelVersionDto modelVersionDto) {

        ModelVersion modelVersion = findByIdOrElseThrow(id);
        this.checkModelVersionStatus(id);
        modelVersion.setMilestoneName(modelVersionDto.getMilestoneName());
        return save(modelVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionDto mapToDto(ModelVersion modelVersion) {
        Assert.notNull(modelVersion, "modelVersion is null");
        modelVersion = findByIdOrElseThrow(modelVersion.getId());
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setId(modelVersion.getId());
        modelVersionDto.setVersionNum(modelVersion.getVersionNum());
        modelVersionDto.setValidFrom(modelVersion.getValidFrom());
        modelVersionDto.setValidTo(modelVersion.getValidTo());
        modelVersionDto.setAsisTobeCode(modelVersion.getAsisTobe().getCode());
        modelVersionDto.setAsisTobeTitle(modelVersion.getAsisTobe().getFaName());
        modelVersionDto.setStatusCode(modelVersion.getStatus().getCode());
        modelVersionDto.setStatusTitle(modelVersion.getStatus().getFaName());
        modelVersionDto.setModelId(modelVersion.getModel().getId());
        modelVersionDto.setLastVersion(modelVersion.getLastVersion());
        if (modelVersion.getXml() != null) {
            modelVersionDto.setXmlFileId(modelVersion.getXml().getId());
        }
        if (modelVersion.getImage() != null) {
            modelVersionDto.setImageFileId(modelVersion.getImage().getId());
        }
        if (modelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkflowFileId(modelVersion.getWorkFlowFile().getId());
        }
        modelVersionDto.setModifiedDate(modelVersion.getModifiedDate());
        if (modelVersion.getParent() != null) {
            modelVersionDto.setParentId(modelVersion.getParent().getId());
        }
        modelVersionDto.setMilestoneName(modelVersion.getMilestoneName());
        modelVersionDto.setCreatedBy(userService.mapToDto(modelVersion.getCreatedBy()));
        modelVersionDto.setModelName(modelVersion.getModel().getName());
        return modelVersionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionDto mapToDtoWithRefData(ModelVersion modelVersion) {
        ModelVersionDto modelVersionDto = mapToDto(modelVersion);
        modelElementReferenceService.findByModelReferenceIdAndReferenceType(modelVersion.getId(), ModelElementRefrence.ReferenceType.INTERNAL.getCode()).ifPresentOrElse((modelElementRefrence) -> {
            modelVersionDto.setParentReferenceModelVersion(mapToDto(modelElementRefrence.getModelVersion()));
        }, () -> {
            modelVersionDto.setParentReferenceModelVersion(null);
        });
        return modelVersionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionDto mapToFilesData(ModelVersion modelVersion) {
        Assert.notNull(modelVersion, "modelVersion is null");
        modelVersion = findByIdOrElseThrow(modelVersion.getId());
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        if (modelVersion.getXml() != null) {
            modelVersionDto.setXmlFileId(modelVersion.getXml().getId());
        }
        if (modelVersion.getImage() != null) {
            modelVersionDto.setImageFileId(modelVersion.getImage().getId());
        }
        if (modelVersion.getWorkFlowFile() != null) {
            modelVersionDto.setWorkflowFileId(modelVersion.getWorkFlowFile().getId());
        }
        return modelVersionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersionDto> mapToDto(List<ModelVersion> modelVersionList) {
        if (CollectionUtils.isEmpty(modelVersionList)) {
            return new ArrayList<>();
        }
        return modelVersionList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionDto mapToIdName(ModelVersion modelVersion) {
        Assert.notNull(modelVersion, "modelVersion is null");
        modelVersion = findByIdOrElseThrow(modelVersion.getId());
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setId(modelVersion.getId());
        modelVersionDto.setModelName(modelVersion.getModel().getName());
        return modelVersionDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersionDto> mapToIdName(List<ModelVersion> modelVersionList) {
        if (CollectionUtils.isEmpty(modelVersionList)) {
            return new ArrayList<>();
        }
        return modelVersionList.stream().map(this::mapToIdName).toList();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long updateXml(MultipartFile file, Long id) {
        FilesDto filesDto = filesService.map(file, Files.FileType.XML);
        ModelVersion modelVersion = findLockedByIdAndOrganId(id);
        this.checkModelVersionStatus(modelVersion.getId());
        if (modelVersion.getXml() == null) {
            Files xml = filesService.create(filesDto);
            filesDto.setId(xml.getId());
            modelVersion.setXml(xml);
            save(modelVersion);
        } else {
            filesDto.setId(modelVersion.getXml().getId());
            filesService.edit(filesDto);
        }
        return filesDto.getId();
    }

    @Override
    public FilesDto getXml(Long id) {
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        if (modelVersion.getXml() == null) throw new BusinessException("error.modelVersion.fileIsNull");
        FilesDto filesDto = filesService.getXml(modelVersion.getXml());
        return filesDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long updateImage(MultipartFile file, Long id) {
        FilesDto filesDto = filesService.map(file, Files.FileType.BYTE);
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        this.checkModelVersionStatus(modelVersion.getId());
        if (modelVersion.getImage() == null) {
            Files image = filesService.create(filesDto);
            filesDto.setId(image.getId());
            modelVersion.setImage(image);
            save(modelVersion);
        } else {
            filesDto.setId(modelVersion.getImage().getId());
            filesService.edit(filesDto);
        }
        return filesDto.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long updateWorkflowFile(MultipartFile file, Long id) {
        FilesDto filesDto = filesService.map(file, Files.FileType.BYTE);
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        this.checkModelVersionStatus(modelVersion.getId());
        if (modelVersion.getWorkFlowFile() == null) {
            Files workflowFile = filesService.create(filesDto);
            filesDto.setId(workflowFile.getId());
            modelVersion.setWorkFlowFile(workflowFile);
            save(modelVersion);
        }  else {
            filesDto.setId(modelVersion.getWorkFlowFile().getId());
            filesService.edit(filesDto);
        }
        return filesDto.getId();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion updateXmlAndImage(MultipartFile xml, MultipartFile image, Long id) {
        ModelVersion modelVersion = findLockedByIdAndOrganId(id);
        this.checkModelVersionStatus(modelVersion.getId());
        updateXml(xml, modelVersion.getId());
        updateImage(image, modelVersion.getId());
        return modelVersion;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelVersion updateWorkFlow(MultipartFile workFlow, Long id) {
        ModelVersion modelVersion = findLockedByIdAndOrganId(id);
        this.checkModelVersionStatus(modelVersion.getId());
        updateWorkflowFile(workFlow,id);
        return modelVersion;
    }


    @Override
    public FilesDto getImage(Long id) {
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        if (modelVersion.getImage() == null) throw new BusinessException("error.modelVersion.fileIsNull");
        FilesDto filesDto = filesService.getByteContent(modelVersion.getImage());
        return filesDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersion> getByResourceSchemaResourceCatalogId(Long resourceCatalogId) {
        return getRepository().getByResourceSchemaResourceCatalogId(resourceCatalogId);
    }

    @Override
    public byte[] exportSipocReport(Long id) {
        //use findByIdOrElseThrow method just for check user access to report
        findByIdOrElseThrow(id);
        String resourceLocation = "reports/sipoc/sipoc_A4.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("MODEL_VERSION_ID", id);
        parameters.put("CURRENT_DATE_PERSIAN", DateUtils.currentDateTimePersianInString());
        return reportGenerator.exportReportToPdf(resourceLocation, parameters);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModelVersion> findByModelIdAndAsisTobeCode(Long modelId, String asisTobeCode) {
        return getRepository().findByModelIdAndAsisTobeCode(modelId, asisTobeCode);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersion getLastVersion(String asisTobe, Long modelId) {
        return getRepository().getLastVersion(asisTobe, modelId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModelVersion> getLastVersionByParent(String asisTobe, Long modelId, Long mvParentId) {
        return getRepository().getLastVersionByParent(asisTobe, modelId, mvParentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersion> getLastVersions(Long modelId) {
        return getRepository().getLastVersions(modelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersion> findByModelIdAndModelStatusCodeNot(Long modelId, String modelStatusCode) {
        return getRepository().findByModelIdAndModelStatusCodeNot(modelId, modelStatusCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModelVersion> getInternalProcessList(List<String> asisTobeCodeList, List<Long> notInModelVersionIdList, Long organId, String modelName, List<String> modelTypeCodeList, Boolean filterHasReference, Pageable pageable) {
        //if pass null to query, error occurred(hibernate bug).this trick prevents error.
        if (modelName == null) {
            modelName = StringUtils.EMPTY;
        }
        if (!filterHasReference) {
            return getRepository().getInternalProcessList(asisTobeCodeList, notInModelVersionIdList, organId, modelName, modelTypeCodeList, pageable);

        }
        return getRepository().getFilterInternalProcessListByElementReference(asisTobeCodeList, notInModelVersionIdList, organId, modelName, modelTypeCodeList, ModelElementRefrence.ReferenceType.INTERNAL.getCode(), pageable);

    }

    /**
     * return last version of model version(if not last version)
     */
    @Override
    @Transactional(readOnly = true)
    public ModelVersion getLastVersion(Long modelVersionId) {
        ModelVersion modelVersion = findByIdOrElseThrow(modelVersionId);
        if (modelVersion.getLastVersion()) {
            return modelVersion;
        }
        Optional<ModelVersion> optionalLastModelVersionByParent = getLastVersionByParent(modelVersion.getAsisTobe().getCode(), modelVersion.getModel().getId(), modelVersion.getParent().getId());
        return optionalLastModelVersionByParent.orElseGet(() -> getLastVersion(ModelVersion.AsisTobe.AS_IS.getCode(), modelVersion.getModel().getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto milestoneList(ModelVersionDto modelVersionDto, int page, int size, String orderBy, Sort.Direction direction) {
        ModelVersion modelVersion = getLastVersion(modelVersionDto.getAsisTobeCode(), modelVersionDto.getModelId());
        if (modelVersion == null || modelVersion.getParent() == null) {
            return new SearchDto.Builder().currentPage(page).pageSize(size).build();
        }
        modelVersionDto.setParentId(modelVersion.getParent().getId());
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ModelVersion>> optionalSpecification = createSpecification(modelVersionDto);
        Page<ModelVersion> modelVersionPage = findAll(optionalSpecification.get(), pageable);
        List<ModelVersionDto> content = modelVersionPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content).totalElements(modelVersionPage.getTotalElements()).totalPages(modelVersionPage.getTotalPages()).currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ModelVersion>> createSpecification(ModelVersionDto modelVersionDto) {
        List<Specification<ModelVersion>> specificationList = new ArrayList<>();

        Specification<ModelVersion> organEq = (root, query, criteriaBuilder) -> {
            Join<ModelVersion, Model> modelJoin = root.join(ModelVersion_.MODEL);
            Join<ModelVersion, ModelVersion> parentJoin = root.join(ModelVersion_.PARENT);
            Join<ModelVersion, CodeCategory> asisTobeJoin = root.join(ModelVersion_.ASIS_TOBE);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[5];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.equal(modelJoin.get(Model_.ID), modelVersionDto.getModelId());
            predicates[3] = criteriaBuilder.equal(parentJoin.get(Model_.ID), modelVersionDto.getParentId());
            predicates[4] = criteriaBuilder.equal(asisTobeJoin.get(CodeCategory_.CODE), modelVersionDto.getAsisTobeCode());
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    public List<ErrorDto> validateModelVersion(Long modelVersionId) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        ModelVersion modelVersion = findByIdOrElseThrow(modelVersionId);
        //step1
        if (!modelVersion.getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())) {
            ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR).message(ResourceBundleUtils.getMessage("error.modelversion.modelMustBeDraft")).build();
            errorDtoList.add(errorDto);
        }
        //step2
        if (modelVersion.getIdentityDataSchema() == null || modelVersion.getIdentityDataSchema().getProcessCategoryId() == null) {
            ErrorDto errorDto = new ErrorDto.Builder().status(ErrorDto.Status.ERROR).message(ResourceBundleUtils.getMessage("error.modelversion.processCategoryIsNull")).build();
            errorDtoList.add(errorDto);
        }
        //step3
        List<IdentityIo> identityIoList = identityIoService.findActiveByModelVersionId(modelVersionId);
        if (!CollectionUtils.isEmpty(identityIoList)) {
            List<ErrorDto> identityIoErrorList = identityIoService.validateIdentityIo(identityIoList);
            if (!CollectionUtils.isEmpty(identityIoErrorList)) {
                errorDtoList.addAll(identityIoErrorList);
            }
            identityIoList.forEach(identityIo -> {
                List<IoSupCust> ioSupCustList = ioSupCustService.findByIdentityIoId(identityIo.getId(), null, null);
                List<ErrorDto> ioSupCustErrorList = ioSupCustService.validateIoSupCust(ioSupCustList);
                if (!CollectionUtils.isEmpty(ioSupCustErrorList)) {
                    errorDtoList.addAll(ioSupCustErrorList);
                }
            });
        }
        return errorDtoList;
    }

    @Override
    public ModelVersion updateStatus(Long id, String forApprove) {
        ModelVersion modelVersion = findByIdOrElseThrow(id);
        modelVersion.setStatus(codeCategoryService.findByCategoryAndCode(ModelVersion.Status.CATEGORY, forApprove));
        return modelVersion;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountByProcessCategoryId(Long processCategoryId) {
        return getRepository().getCountByProcessCategoryId(processCategoryId);
    }

    public ModelVersionDto countInvalidModels(ModelVersionDto modelDto)
    {
        ModelVersionDto newModelVersionDto = new ModelVersionDto();
        newModelVersionDto.setNoCategoryCount(modelVersionRepository.getcountModelNoProcessCategory( SecurityUtils.getCurrentOrganIdOrElseThrow()));
        newModelVersionDto.setNoSIPOCValidCount(modelVersionRepository.getcountSIPOCInvalidModel( SecurityUtils.getCurrentOrganIdOrElseThrow()));
        newModelVersionDto.setNoInputCount(modelVersionRepository.getCountModelsWithoutIdentityType("INPUT", SecurityUtils.getCurrentOrganIdOrElseThrow(),modelDto.getModelName()));
        newModelVersionDto.setNoOutputCount(modelVersionRepository.getCountModelsWithoutIdentityType("OUTPUT", SecurityUtils.getCurrentOrganIdOrElseThrow(),modelDto.getModelName()));
        newModelVersionDto.setNoSupplierCount(modelVersionRepository.getcountModelsWithoutCustomerSuplier("INPUT", modelDto.getModelName(), SecurityUtils.getCurrentOrganIdOrElseThrow()));
        newModelVersionDto.setNoCustomerCount(modelVersionRepository.getcountModelsWithoutCustomerSuplier( "OUTPUT", modelDto.getModelName(), SecurityUtils.getCurrentOrganIdOrElseThrow()));
        return  newModelVersionDto;
    }

    @Override
    public Optional<ModelVersion> findModelVersionOfModelApprover(Long modelApproverId) {
        return this.getRepository().findModelVersionOfModelApprover(modelApproverId);
    }
    @Override
    public Optional<ModelVersion> findModelVersionOfModelElementReferenceId(Long modelElementReferenceId) {
        return this.getRepository().findModelVersionOfModelElementReferenceId(modelElementReferenceId);
    }
    @Override
    public Optional<ModelVersion> findModelVersionOfModelMemberRole(Long id) {
        return this.getRepository().findModelVersionOfModelMemberRole(id);
    }
    @Override
    public void checkModelVersionStatus(Long modelVersionId) {
        Optional<ModelVersion> modelVersion = this.findById(modelVersionId);
        if (!modelVersion.get().getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())) {
            throw new BusinessException("error.modelversion.canNotChangeNoDraftModelVersion");
        }
    }

    @Override
    public ModelElementRefrence findModelVersionOfProcessCategoryModelElement(Long id) {
        return this.getRepository().findModelVersionOfProcessCategoryModelElement(id);
    }


    @Transactional(readOnly = true)
    public SearchDto listInvalidModels(ModelVersionDto modelDto, int page, int size, String orderBy, Sort.Direction direction) {
        orderBy = "id";
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
       // modelDto.setValidationType("NO_CATEGORY");
        Page<ModelVersion> modelPage = null;
        List<ModelVersionDto> content = null;
        if (modelDto.getValidationType().equals("NO_CATEGORY") || modelDto.getValidationType().equals("NO_SIPOCVALID")) {
            Page<Tuple> modelTotalTuples = null;

            if(modelDto.getValidationType().equals("NO_CATEGORY"))
                modelTotalTuples =  modelVersionRepository.listModelNoProcessCategory(pageable,   SecurityUtils.getCurrentOrganIdOrElseThrow());
            else if (modelDto.getValidationType().equals("NO_SIPOCVALID"))
                modelTotalTuples = modelVersionRepository.listSIPOCInvalidModel(pageable,  SecurityUtils.getCurrentOrganIdOrElseThrow());

            //  List<Tuple> stockTotalTuples = stockRepository.findStocktotal();

            content = modelTotalTuples.stream()
                    .map(t -> new ModelVersionDto(
                            (Integer) t.get(0),
                            (Integer) t.get(1),//.get(0, Long.class),
                            (String) t.get(2),//.get(1, String.class),
                            (String) codeCategoryService.findByIdOrElseThrow(Long.valueOf(t.get(3).toString())).getFaName()//.get(2, Long.class)
                    ))
                    .collect(Collectors.toList());

            return new SearchDto.Builder().content(content)
                    .totalElements(modelTotalTuples.getTotalElements())
                    .totalPages(modelTotalTuples.getTotalPages())
                    .currentPage(page)
                    .pageSize(size).build();

        } else
        {
            if (modelDto.getValidationType().equals("NO_INPUT")) {
                modelPage = modelVersionRepository.listModelsWithoutIdentityType(pageable, "INPUT"//, modelDto.getName()
                        , SecurityUtils.getCurrentOrganIdOrElseThrow(),modelDto.getModelName());
            } else if (modelDto.getValidationType().equals("NO_OUTPUT")) {
                modelPage = modelVersionRepository.listModelsWithoutIdentityType(pageable, "OUTPUT"//, modelDto.getName()
                        , SecurityUtils.getCurrentOrganIdOrElseThrow(),modelDto.getModelName());
            } else if (modelDto.getValidationType().equals("NO_SUPPLIER")) {
                modelPage = modelVersionRepository.listModelsWithoutCustomerSuplier(pageable, "INPUT", modelDto.getModelName(), SecurityUtils.getCurrentOrganIdOrElseThrow());
            } else if (modelDto.getValidationType().equals("NO_CUSTOMER")) {
                modelPage = modelVersionRepository.listModelsWithoutCustomerSuplier(pageable, "OUTPUT", modelDto.getModelName(), SecurityUtils.getCurrentOrganIdOrElseThrow());
            }


            content = modelPage.map(this::mapToDto).getContent();
            return new SearchDto.Builder().content(content)
                    .totalElements(modelPage.getTotalElements())
                    .totalPages(modelPage.getTotalPages())
                    .currentPage(page)
                    .pageSize(size).build();
        }


    }



}
