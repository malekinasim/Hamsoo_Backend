package com.hamsoo.bpm.service.impl.modelcomment;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.modelversion.TeamWorkDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment_;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.user.User_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelcomment.ModelCommentRepository;
import com.hamsoo.bpm.service.api.modelcomment.ModelCommentService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT;

@Service
public class ModelCommentServiceImpl implements ModelCommentService {

    private final ModelCommentRepository modelCommentRepository;
    private final ModelVersionService modelVersionService;
    private final UserService userService;

    private final ModelElementReferenceService modelElementReferenceService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    public ModelCommentServiceImpl(ModelCommentRepository modelCommentRepository,
                                   ModelVersionService modelVersionService,
                                   UserService userService,
                                   ModelElementReferenceService modelElementReferenceService, SimpMessagingTemplate simpMessagingTemplate) {
        this.modelCommentRepository = modelCommentRepository;
        this.modelVersionService = modelVersionService;
        this.userService = userService;

        this.modelElementReferenceService = modelElementReferenceService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public ModelCommentRepository getRepository() {
        return modelCommentRepository;
    }

    @Override
    public ModelComment findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelComment.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.modelComment.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelComment create(ModelCommentDto modelCommentDto) {
        Assert.notNull(modelCommentDto, "modelCommentDto is null");
        //workspace admin and supervisor must add comment
        //  modelMemberService.checkUserMemberOfModel(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelCommentDto.getModelVersionId());
        modelVersionService.checkModelVersionStatus(modelCommentDto.getModelVersionId());
        ModelComment modelComment = new ModelComment();

        modelComment.setUser(userService.findByUserIdAndCurrentOrganOrElseThrow(SecurityUtils.getCurrentUserOrElseThrow().getId()));
        modelComment.setText(modelCommentDto.getText());
        modelComment.setModelVersion(modelVersionService.findByIdOrElseThrow(modelCommentDto.getModelVersionId()));
        modelComment.setStatus(Status.ACTIVE.name());

        modelComment = save(modelComment);
        sendCommentNotify(modelComment.getModelVersion().getId(), modelComment.getText());
        return modelComment;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelComment edit(ModelCommentDto modelCommentDto) {
        Assert.notNull(modelCommentDto, "modelCommentDto is null");
        ModelComment modelComment = findByIdOrElseThrow(modelCommentDto.getId());
        if (!modelComment.getUser().getId().equals(SecurityUtils.getCurrentUserOrElseThrow().getId())) {
            throw new BusinessException("error.modelComment.userCannotEditOtherUserComment");
        }
        //workspace admin and supervisor must add comment
        //modelMemberService.checkUserMemberOfModel(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelComment.getModelVersion().getId());
        modelVersionService.checkModelVersionStatus(modelComment.getModelVersion().getId());
        modelComment.setText(modelCommentDto.getText());
        modelComment = save(modelComment);
        sendCommentNotify(modelComment.getModelVersion().getId(), modelComment.getText());
        return modelComment;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        ModelComment modelComment = findByIdOrElseThrow(id);
        if (!modelComment.getUser().getId().equals(SecurityUtils.getCurrentUserOrElseThrow().getId())) {
            throw new BusinessException("error.modelComment.userCannotDeleteOtherUserComment");
        }
        //workspace admin and supervisor must add comment
        // modelMemberService.checkUserMemberOfModel(SecurityUtils.getCurrentUserOrElseThrow().getId(), modelComment.getModelVersion().getId());
        modelVersionService.checkModelVersionStatus(modelComment.getModelVersion().getId());
        List<ModelElementRefrence> modelElementCommentList =
                modelElementReferenceService.findLastByReferenceTypeCodeAndReferenceId(ModelElementRefrence.ReferenceType.COMMENT.getCode(), modelComment.getId());
        if (!CollectionUtils.isEmpty(modelElementCommentList)) {
            throw new BusinessException("error.modelComment.commentHasReference");
        }
        modelComment.setStatus(Status.PASSIVE.name());

        sendCommentNotify(modelComment.getModelVersion().getId(), modelComment.getText());
        save(modelComment);


    }

    private void sendCommentNotify(Long modelVersionId, String commentText) {
        UserDto userDto = SecurityUtils.getCurrentUserOrElseThrow();
        AccountDto accountDto = SecurityUtils.getCurrentAccountOrElseThrow();
        String commentMessage = ResourceBundleUtils.getMessage("title.teamwork.commentModel", userDto.getFullName(), commentText);
        TeamWorkDto teamWorkDto = new TeamWorkDto(commentMessage, userDto.getFullName(), accountDto.getUsername(), TeamWorkDto.MessageType.COMMENT, modelVersionId.toString());

        simpMessagingTemplate.convertAndSend(MessageFormat.format(TEAM_WORK_END_POINT, modelVersionId.toString()), teamWorkDto);

    }

    @Override
    @Transactional(readOnly = true)
    public ModelCommentDto mapToDto(ModelComment modelComment) {
        Assert.notNull(modelComment, "modelComment is null");
        modelComment = findByIdOrElseThrow(modelComment.getId());
        ModelCommentDto modelCommentDto = new ModelCommentDto();
        modelCommentDto.setId(modelComment.getId());
        modelCommentDto.setUserDto(userService.mapToDto(modelComment.getUser()));
        modelCommentDto.setText(modelComment.getText());
        modelCommentDto.setModelVersionId(modelComment.getModelVersion().getId());
        modelCommentDto.setCreatedDate(modelComment.getCreatedDate());
        modelCommentDto.setModifiedDate(modelComment.getModifiedDate());
        return modelCommentDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelCommentDto> mapToDto(List<ModelComment> modelCommentList) {
        if (CollectionUtils.isEmpty(modelCommentList)) {
            return new ArrayList<>();
        }
        return modelCommentList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(Long modelVersionId, ModelCommentDto modelCommentDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<ModelComment>> optionalSpecification = createSpecification(modelVersionId, modelCommentDto);
        Page<ModelComment> modelMemberPage = findAll(optionalSpecification.get(), pageable);
        List<ModelCommentDto> content = modelMemberPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelMemberPage.getTotalElements())
                .totalPages(modelMemberPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<ModelComment>> createSpecification(Long modelVersionId, ModelCommentDto modelCommentDto) {
        List<Specification<ModelComment>> specificationList = new ArrayList<>();

        var wrapper = new Object() {
            Join<ModelComment, User> userJoin;
        };
        Specification<ModelComment> organEq = (root, query, criteriaBuilder) -> {
            Join<ModelComment, ModelVersion> modelVersionJoin = root.join(ModelComment_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            wrapper.userJoin = root.join(ModelComment_.USER);

            Predicate[] predicates = new Predicate[4];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2] = criteriaBuilder.equal(root.get(ModelComment_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            if (modelCommentDto.getUserId() != null) {
                Predicate modelMemberEq = criteriaBuilder.equal(wrapper.userJoin.get(User_.ID), modelCommentDto.getUserId());
                predicates = ArrayUtils.addAll(predicates, modelMemberEq);
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);

        if (StringUtils.hasText(modelCommentDto.getSearchText())) {
            Specification<ModelComment> searchTextLike = (root, query, criteriaBuilder) -> {
                Predicate[] predicates = new Predicate[2];
                predicates[0] = criteriaBuilder.like(wrapper.userJoin.get(User_.FULL_NAME), "%" + modelCommentDto.getSearchText() + "%");
                predicates[1] = criteriaBuilder.like(root.get(ModelComment_.TEXT), "%" + modelCommentDto.getSearchText() + "%");
                return criteriaBuilder.or(predicates);
            };
            specificationList.add(searchTextLike);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId) {
        Assert.notNull(newModelVersionId, "newModelVersionId is null");
        Assert.notNull(oldModelVersionId, "oldModelVersionId is null");
        getRepository().updateModelVersionId(newModelVersionId, oldModelVersionId);
    }

    @Override
    public Page<ModelCommentDto> getCommentAndElementCommentListOfLastVersion(Long modelVersionId, Pageable pageable) {
        return getRepository().getCommentAndElementCommentListOfLastVersion(ModelElementRefrence.ReferenceType.COMMENT.getCode(),
                modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable);
    }


    @Override
    public Page<ModelCommentDto> getElementCommentListOfLastVersion(Long modelVersionId, String elementId, Pageable pageable) {
        return getRepository().getElementCommentListOfLastVersion(ModelElementRefrence.ReferenceType.COMMENT.getCode(),
                modelVersionId, elementId, SecurityUtils.getCurrentOrganIdOrElseThrow(), pageable);
    }

    @Override
    public SearchDto getCommentAndElementCommentListOfLastVersionWithPageData(Long modelVersionId, Pageable pageable) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelComment.modelVersionIdIsNotLastVersion");
        }
        Page<ModelCommentDto> ModelCommentDtoPage = getCommentAndElementCommentListOfLastVersion(modelVersionId, pageable);
        return new SearchDto.Builder().content(ModelCommentDtoPage.getContent())
                .totalElements(ModelCommentDtoPage.getTotalElements())
                .totalPages(ModelCommentDtoPage.getTotalPages())
                .currentPage(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    @Override
    public List<ModelCommentDto> getAllElementCommentListOfLastVersion(Long modelVersionId, String elementId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelComment.modelVersionIdIsNotLastVersion");
        }

        return getRepository().getElementCommentListOfLastVersion(ModelElementRefrence.ReferenceType.COMMENT.getCode(),
                modelVersionId, elementId, SecurityUtils.getCurrentOrganIdOrElseThrow());


    }
}
