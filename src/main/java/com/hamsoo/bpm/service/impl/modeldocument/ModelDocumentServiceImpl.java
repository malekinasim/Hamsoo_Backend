package com.hamsoo.bpm.service.impl.modeldocument;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.modeldocument.ModelDocumentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument;
import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modeldocument.ModelDocumentRepository;
import org.apache.commons.lang3.ArrayUtils;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ModelDocumentServiceImpl implements ModelDocumentService {

    private final ModelDocumentRepository modelDocumentRepository;
    private final FilesService filesService;
    private final ModelVersionService modelVersionService;
    private final UserService userService;
    private final OrganPlanService organPlanService;

    public ModelDocumentServiceImpl(ModelDocumentRepository modelDocumentRepository,
                                    FilesService filesService,
                                    ModelVersionService modelVersionService, UserService userService,
                                    @Lazy OrganPlanService organPlanService) {
        this.modelDocumentRepository = modelDocumentRepository;
        this.filesService = filesService;
        this.modelVersionService = modelVersionService;
        this.userService = userService;
        this.organPlanService = organPlanService;
    }

    @Override
    public ModelDocumentRepository getRepository() {
        return modelDocumentRepository;
    }

    @Override
    public ModelDocument findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelDocument.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.modelDocument.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelDocument create(ModelDocumentDto modelDocumentDto) {
        Assert.notNull(modelDocumentDto, "modelDocumentDto is null");
        organPlanService.CheckOrganCapacityLimitBaseOnLastOrganPlan(SecurityUtils.getCurrentOrganIdOrElseThrow());
        modelVersionService.checkModelVersionStatus(modelDocumentDto.getModelVersionId());
        ModelDocument modelDocument = new ModelDocument();
        modelDocument.setFile(filesService.create(modelDocumentDto.getFilesDto()));
        modelDocument.setModelVersion(modelVersionService.findByIdOrElseThrow(modelDocumentDto.getModelVersionId()));
        modelDocument.setName(modelDocumentDto.getName());
        modelDocument.setDescription(modelDocumentDto.getDescription());
        modelDocument.setElementId(modelDocumentDto.getElementId());
        return save(modelDocument);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        ModelDocument modelDocument = findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(modelDocument.getModelVersion().getId());
        Long fileId = modelDocument.getFile().getId();
        delete(modelDocument);
        List<ModelDocument> modelDocumentList = getRepository().findByFileId(fileId);
        if (CollectionUtils.isEmpty(modelDocumentList)) {
            filesService.delete(fileId);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveByElementIdAndModelVersion(Long modelVersionId, String elementId) {
        List<ModelDocument> documents = getRepository().findAllByModelVersion_IdAndElementId(modelVersionId, elementId);
        modelVersionService.checkModelVersionStatus(modelVersionId);
        documents.forEach(document -> delete(document.getId()));
    }

    @Override
    public BigInteger getOrganModelDocumentCapacity(Long id) {
       return this.getRepository().calculateOrganModelDocumentCapacity(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelDocumentDto mapToDto(ModelDocument modelDocument) {
        Assert.notNull(modelDocument, "modelDocument is null");
        modelDocument = findByIdOrElseThrow(modelDocument.getId());
        ModelDocumentDto modelDocumentDto = new ModelDocumentDto();
        modelDocumentDto.setId(modelDocument.getId());
        modelDocumentDto.setFilesDto(filesService.mapToDto(modelDocument.getFile()));
        modelDocumentDto.setModelVersionId(modelDocument.getModelVersion().getId());
        modelDocumentDto.setName(modelDocument.getName());
        modelDocumentDto.setDescription(modelDocument.getDescription());
        modelDocumentDto.setElementId(modelDocument.getElementId());
        modelDocumentDto.setCreateBy(userService.mapToDto(modelDocument.getCreatedBy()));
        modelDocumentDto.setCreatedDate(modelDocument.getCreatedDate());
        modelDocumentDto.setModifiedDate(modelDocument.getModifiedDate());
        return modelDocumentDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ModelDocumentDto modelDocumentDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ModelDocument>> optionalSpecification = createSpecification(modelDocumentDto);
        Page<ModelDocument> modelDocumentPage = findAll(optionalSpecification.get(), pageable);
        List<ModelDocumentDto> content = modelDocumentPage.map(this::mapToDto).getContent();


        return new SearchDto.Builder().content(content)
                .totalElements(modelDocumentPage.getTotalElements())
                .totalPages(modelDocumentPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ModelDocument>> createSpecification(ModelDocumentDto modelDocumentDto) {
        Assert.notNull(modelDocumentDto.getModelVersionId(), "modelVersionId is null");
        List<Specification<ModelDocument>> specificationList = new ArrayList<>();

        Specification<ModelDocument> organEq = (root, query, criteriaBuilder) -> {
            Join<ModelDocument, ModelVersion> modelVersionJoin = root.join(ModelDocument_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelDocumentDto.getModelVersionId());
            predicates[2] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());

            if (StringUtils.hasText(modelDocumentDto.getElementId())) {
                Predicate modelMemberEq = criteriaBuilder.equal(root.get(ModelDocument_.ELEMENT_ID), modelDocumentDto.getElementId());
                if (modelDocumentDto.getElementId().equals("null"))
                    modelMemberEq = criteriaBuilder.isNull(root.get(ModelDocument_.ELEMENT_ID));
                predicates = ArrayUtils.addAll(predicates, modelMemberEq);
            } else {
                Predicate modelMemberEq = criteriaBuilder.isNotNull(root.get(ModelDocument_.ELEMENT_ID));
                predicates = ArrayUtils.addAll(predicates, modelMemberEq);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelDocument> findByModelVersionId(Long modelVersionId) {
        return getRepository().findByModelVersionId(modelVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public void copyModelDocumentsIfExist(Long newModelVersionId, Long oldModelVersionId) {
        List<ModelDocument> oldModelDocumentList = getRepository().findByModelVersionId(oldModelVersionId);
        if (CollectionUtils.isEmpty(oldModelDocumentList)) {
            return;
        }
        oldModelDocumentList.forEach(oldModelDocument -> {
            ModelDocument newModelDocument = new ModelDocument();
            newModelDocument.setModelVersion(modelVersionService.findByIdOrElseThrow(newModelVersionId));
            newModelDocument.setFile(filesService.findByIdOrElseThrow(oldModelDocument.getFile().getId()));
            newModelDocument.setName(oldModelDocument.getName());
            newModelDocument.setDescription(oldModelDocument.getDescription());
            save(newModelDocument);
        });
    }


}
