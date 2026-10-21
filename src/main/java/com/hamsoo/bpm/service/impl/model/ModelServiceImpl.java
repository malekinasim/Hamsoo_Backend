package com.hamsoo.bpm.service.impl.model;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.BpmnHelperService;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.job.Job_;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember_;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole_;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole_;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.person.Person_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.post.Post_;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.postmember.PostMember_;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.unit.Unit_;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.user.User_;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite_;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.model.ModelRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.processcategory.ProcessCategoryModelElementService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userfavorite.UserFavoriteService;
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

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final CodeCategoryService codeCategoryService;
    private final UnitService unitService;
    private final UserFavoriteService userFavoriteService;
    private final IoSupCustService ioSupCustService;
    private final ModelMemberService modelMemberService;
    private final ModelMemberRoleService modelMemberRoleService;
    private final ModelVersionService modelVersionService;
    private final OrganService organService;
    private final ModelElementReferenceService modelElementReferenceService;
    private final ProcessCategoryModelElementService processCategoryModelElementService;
    private final UserService userService;


    public ModelServiceImpl(ModelRepository modelRepository,
                            CodeCategoryService codeCategoryService,
                            UnitService unitService,
                            UserFavoriteService userFavoriteService,
                            IoSupCustService ioSupCustService,
                            ModelMemberService modelMemberService,
                            ModelMemberRoleService modelMemberRoleService,
                            @Lazy ModelVersionService modelVersionService,
                            OrganService organService,
                            ModelElementReferenceService modelElementReferenceService,
                            ProcessCategoryModelElementService processCategoryModelElementService, UserService userService) {
        this.modelRepository = modelRepository;
        this.codeCategoryService = codeCategoryService;
        this.unitService = unitService;
        this.userFavoriteService = userFavoriteService;
        this.ioSupCustService = ioSupCustService;
        this.modelMemberService = modelMemberService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.modelVersionService = modelVersionService;
        this.organService = organService;
        this.modelElementReferenceService = modelElementReferenceService;
        this.processCategoryModelElementService = processCategoryModelElementService;
        this.userService = userService;
    }

    @Override
    public ModelRepository getRepository() {
        return modelRepository;
    }

    @Override
    public Model findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.model.idIsNotValid");
        }
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.model.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Model create(ModelDto modelDto) {
        Assert.notNull(modelDto, "modelDto is null");
        Model model = new Model();
        model.setName(modelDto.getName());
        model.setModelType(codeCategoryService.findByCategoryAndCode(Model.ModelType.CATEGORY, modelDto.getModelTypeCode()));
        model.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        model.setStatus(Status.ACTIVE.name());
        if (modelDto.getResponsibleUnitId() != null) {
            model.setResponsibleUnit(unitService.findByIdOrElseThrow(modelDto.getResponsibleUnitId()));
        }
        model = save(model);
        String code = modelDto.getModelTypeCode().concat("-").concat(model.getId().toString());
        validateCode(code);
        model.setCode(code);
        return save(model);
    }

    private void validateCode(String code) {
        Optional<Model> optionalModel = findByCodeAndOrganId(code, SecurityUtils.getCurrentOrganIdOrElseThrow());
        if (optionalModel.isPresent()) {
            throw new BusinessException("error.model.modelCodeIsDuplicate");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Model updateCode(Long id, String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException("error.model.codeIsNotValid");
        }
        Model model = findByIdOrElseThrow(id);
        if (model.getCode() != null && model.getCode().equals(code)) {
            return model;
        }
        validateCode(code);
        model.setCode(code);
        return save(model);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        Model model = findByIdOrElseThrow(id);
        //model in draft status can passive
        List<ModelVersion> modelVersionList = modelVersionService.findByModelIdAndModelStatusCodeNot(id, ModelVersion.Status.DRAFT.getCode());
        if (!CollectionUtils.isEmpty(modelVersionList)) {
            throw new BusinessException("error.model.modelNotInDraftStatus");
        }
        //just owner can passive model
        List<ModelMember> modelMemberList = modelMemberService.findByModelIdAndModelRoleCodeAndUserId(id,
                ModelRole.Code.MODEL_OWNER.getCode(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (CollectionUtils.isEmpty(modelMemberList)) {
            throw new BusinessException("error.model.justOwnerCanPassiveModel");
        }
        //model's model versions shouldn't exist in other model identities as supplier or customer
        Long count = ioSupCustService.countInternalSupCustByModelId(id);
        if (count > 0) {
            throw new BusinessException("error.model.modelExistInIdentity");
        }
        //model's model versions shouldn't reference in other model.
        Long referencedModelCount = modelElementReferenceService.countByReferencedModelId(id);
        if (referencedModelCount > 0) {
            throw new BusinessException("error.model.modelReferenced");
        }
        if (model.getModelType().getCode().equals(Model.ModelType.ENTERPRISE_MAP.getCode())) {
            processCategoryModelElementService.deleteByModelId(id);
        }
        model.setStatus(Status.PASSIVE.name());
        save(model);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Model rename(Long id, String name) {
        Assert.notNull(name, "name is null");
        Model model = findByIdOrElseThrow(id);
        model.setName(name);
        return save(model);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean toggleBookmarkState(Long id) {
        Model model = findByIdOrElseThrow(id);
        List<UserFavorite> userFavoriteList =
                userFavoriteService.findByReferenceTypeAndReferenceIdAndUserId(UserFavorite.ReferenceType.MODEL.name(),
                        model.getId(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (CollectionUtils.isEmpty(userFavoriteList)) {
            UserFavoriteDto userFavoriteDto = new UserFavoriteDto();
            userFavoriteDto.setReferenceType(UserFavorite.ReferenceType.MODEL.name());
            userFavoriteDto.setReferenceId(model.getId());
            userFavoriteService.create(userFavoriteDto);
            return true;
        } else {
            userFavoriteService.delete(userFavoriteList.get(0).getId(), true);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ModelDto mapToDto(Model model) {
        Assert.notNull(model, "model is null");
        Assert.notNull(model.getId(), "model's id is null");

        model = findByIdOrElseThrow(model.getId());
        ModelDto modelDto = new ModelDto();
        modelDto.setId(model.getId());
        modelDto.setName(model.getName());
        modelDto.setCode(model.getCode());
        modelDto.setModelTypeCode(model.getModelType().getCode());
        modelDto.setModelTypeTitle(model.getModelType().getFaName());
        modelDto.setModifiedDate(model.getModifiedDate());
        modelDto.setOrganId(model.getOrgan().getId());
        modelDto.setOrganName(model.getOrgan().getName());
        modelDto.setModelLastModificationDate(model.getModelLastModificationDate());
        if (model.getResponsibleUnit() != null) {
            modelDto.setResponsibleUnit(unitService.mapToDto(model.getResponsibleUnit()));
        }
        return modelDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ModelDto mapToDtoWithLastVersionData(Model model) {
        ModelDto modelDto = mapToDto(model);
        ModelVersion lastAsisVersion = modelVersionService.getRepository()
                .getLastVersion(ModelVersion.AsisTobe.AS_IS.getCode(), model.getId());
        if (lastAsisVersion != null) {
            modelDto.setLastAsisVersion(modelVersionService.mapToDtoWithRefData(lastAsisVersion));

            List<ModelMemberRole> modelMemberRoleList = modelMemberRoleService.findByModelVersionIdAndUserIdAndStatus(lastAsisVersion.getId(),
                    SecurityUtils.getCurrentUserOrElseThrow().getId(), Status.ACTIVE.name());

            modelDto.getLastAsisVersion().setModelMemberDtoList(modelMemberRoleService.mapToDto(modelMemberRoleList));
        }

        ModelVersion lastTobeVersion = modelVersionService.getRepository()
                .getLastVersion(ModelVersion.AsisTobe.TO_BE.getCode(), model.getId());
        if (lastTobeVersion != null) {
            modelDto.setLastTobeVersion(modelVersionService.mapToDtoWithRefData(lastTobeVersion));

            List<ModelMemberRole> modelMemberRoleList = modelMemberRoleService.findByModelVersionIdAndUserIdAndStatus(lastTobeVersion.getId(),
                    SecurityUtils.getCurrentUserOrElseThrow().getId(), Status.ACTIVE.name());

            modelDto.getLastTobeVersion().setModelMemberDtoList(modelMemberRoleService.mapToDto(modelMemberRoleList));
        }
        List<UserFavorite> userFavoriteList = userFavoriteService.findByReferenceTypeAndReferenceIdAndUserId(UserFavorite.ReferenceType.MODEL.name(),
                model.getId(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (!CollectionUtils.isEmpty(userFavoriteList)) {
            modelDto.setFavorite(true);
            modelDto.setUserFavoriteDto(userFavoriteService.mapToDto(userFavoriteList.get(0)));
        } else {
            modelDto.setFavorite(false);
        }

        return modelDto;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Model> findByCodeAndOrganId(String code, Long organId) {
        return getRepository().findByCodeAndOrganId(code, organId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Model> findByModelTypeCodeAndOrganId(String modelTypeCode, Long organId) {
        return getRepository().findByModelTypeCodeAndOrganId(modelTypeCode, organId);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Model>> optionalSpecification = createSpecification(modelDto, true);
        Page<Model> modelPage = findAll(optionalSpecification.get(), pageable);
        List<ModelDto> content = modelPage.map(this::mapToDtoWithLastVersionData).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page)
                .pageSize(size).build();
    }


    public Optional<Specification<Model>> createSpecification(ModelDto modelDto, boolean filterByMininVersion) {
        List<Specification<Model>> specificationList = new ArrayList<>();
         //not filter based on model_member for   ADMIN_ROLE and SUPERVISOR_ROLE
        if (!SecurityUtils.hasAnyAuthority(Role.OrganRole.SUPERVISOR_ROLE.getCode(),Role.OrganRole.ADMIN_ROLE.getCode())) {
            Optional<Specification<Model>> modelExistSpecification=modelExist(modelDto);
            if(modelExistSpecification.isPresent())
                   specificationList.add(modelExistSpecification.get());
        }

        specificationList.add(modelVersionPredicates(modelDto, filterByMininVersion));

        Specification<Model> organEq = (root, query, criteriaBuilder) -> {
            Join<Model, Organ> organJoin = root.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(root.get(Model_.STATUS), Status.ACTIVE.name());
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);

        if (modelDto.getName() != null) {
            Specification<Model> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(Model_.NAME), "%" + modelDto.getName() + "%");
            specificationList.add(nameLike);
        }

        if (!CollectionUtils.isEmpty(modelDto.getModelTypeCodeList())) {
            Specification<Model> modelTypeCodeIn = (root, query, criteriaBuilder) -> {
                Join<Model, CodeCategory> modelTypeJoin = root.join(Model_.MODEL_TYPE);
                return modelTypeJoin.get(CodeCategory_.CODE).in(modelDto.getModelTypeCodeList());
            };
            specificationList.add(modelTypeCodeIn);
        }

        if (modelDto.getFavorite() != null && modelDto.getFavorite()) {
            specificationList.add(userFavoriteExist());
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }
    private Optional<Specification<Model>> modelExist(ModelDto modelDto) {
        List<Specification<Model>> specificationList = new ArrayList<>();
        specificationList.add(modelExistByUserIdFilter(modelDto));
        specificationList.add(modelExistByPostOfUserFilter(modelDto));
        specificationList.add(modelExistByJobOfUserFilter(modelDto));
        specificationList.add(modelExistByUnitOfUserFilter(modelDto));
        return SpecificationUtils.orSpecifications(specificationList);
    }
    private Specification<Model> modelExistByUserIdFilter(ModelDto modelDto) {
        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelRole> modeRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember,CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[7];

            predicates[0] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.MEMBER_ID), SecurityUtils.getCurrentUserOrElseThrow().getId());
            predicates[1] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.USER.getCode());
            predicates[2] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.notEqual(modeRoleJoin.get(ModelRole_.CODE), ModelRole.Code.MODEL_APPROVER.getCode());
            predicates[4] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.STATUS), Status.ACTIVE.name());
            predicates[5] = criteriaBuilder.equal(modelJoin.get(Model_.ID), root.get(Model_.ID));
            predicates[6] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);

            if (!CollectionUtils.isEmpty(modelDto.getModelRoleCodeList())) {
                Join<ModelMemberRole, ModelRole> modelRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
                Predicate modelRoleInPredicate = modelRoleJoin.get(ModelRole_.CODE).in(modelDto.getModelRoleCodeList());
                predicates = ArrayUtils.addAll(predicates, modelRoleInPredicate);
            }

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }
    private Specification<Model> modelExistByPostOfUserFilter(ModelDto modelDto) {
        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelRole> modeRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember,CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);
            Join<Organ, Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit>   unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Unit, Post>   postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember>   postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person>   personJoin = postMemberJoin.join(PostMember_.PERSON);

            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[18];

            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.notEqual(modeRoleJoin.get(ModelRole_.CODE), ModelRole.Code.MODEL_APPROVER.getCode());
            predicates[3] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(modelJoin.get(Model_.ID), root.get(Model_.ID));
            predicates[5] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            predicates[6] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[8] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());
            predicates[9] = criteriaBuilder.equal(postJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[10] = criteriaBuilder.equal(postMemberJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[11] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[12] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[13] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[14] = criteriaBuilder.equal(userJoin.get(User_.ID), SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[15] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE), userJoin.get(User_.NATIONAL_CODE));
            predicates[16] = criteriaBuilder.equal(postJoin.get(Post_.ID), modelMemberJoin.get(ModelMember_.MEMBER_ID));
            predicates[17] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.POST.getCode());

            if (!CollectionUtils.isEmpty(modelDto.getModelRoleCodeList())) {
                Join<ModelMemberRole, ModelRole> modelRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
                Predicate modelRoleInPredicate = modelRoleJoin.get(ModelRole_.CODE).in(modelDto.getModelRoleCodeList());
                predicates = ArrayUtils.addAll(predicates, modelRoleInPredicate);
            }

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }
    private Specification<Model> modelExistByJobOfUserFilter(ModelDto modelDto) {
        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelRole> modeRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember,CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);

            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);

            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);

            Join<Organ, Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit>   unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Chart, Job>   jobJoin = chartJoin.joinList(Chart_.JOBS);
            Join<Unit, Post>   postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember>   postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person>   personJoin = postMemberJoin.join(PostMember_.PERSON);

            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[19];

            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.notEqual(modeRoleJoin.get(ModelRole_.CODE), ModelRole.Code.MODEL_APPROVER.getCode());
            predicates[3] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(modelJoin.get(Model_.ID), root.get(Model_.ID));
            predicates[5] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            predicates[6] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[8] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());
            predicates[9] = criteriaBuilder.equal(jobJoin.get(Job_.STATUS), Status.ACTIVE.name());
            predicates[10] = criteriaBuilder.equal(postJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[11] = criteriaBuilder.equal(postMemberJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[12] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[13] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[14] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[15] = criteriaBuilder.equal(userJoin.get(User_.ID), SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[16] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE), userJoin.get(User_.NATIONAL_CODE));
            predicates[17] = criteriaBuilder.equal(jobJoin.get(Job_.ID), modelMemberJoin.get(ModelMember_.MEMBER_ID));
            predicates[18] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.JOB.getCode());

            if (!CollectionUtils.isEmpty(modelDto.getModelRoleCodeList())) {
                Join<ModelMemberRole, ModelRole> modelRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
                Predicate modelRoleInPredicate = modelRoleJoin.get(ModelRole_.CODE).in(modelDto.getModelRoleCodeList());
                predicates = ArrayUtils.addAll(predicates, modelRoleInPredicate);
            }

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }
    private Specification<Model> modelExistByUnitOfUserFilter(ModelDto modelDto) {
        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelRole> modeRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember,CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);

            Join<Organ, Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit>   unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Unit, Post>   postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember>   postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person>   personJoin = postMemberJoin.join(PostMember_.PERSON);

            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[18];

            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.notEqual(modeRoleJoin.get(ModelRole_.CODE), ModelRole.Code.MODEL_APPROVER.getCode());
            predicates[3] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(modelJoin.get(Model_.ID), root.get(Model_.ID));
            predicates[5] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            predicates[6] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());
            predicates[8] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());

            predicates[9] = criteriaBuilder.equal(postJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[10] = criteriaBuilder.equal(postMemberJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[11] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[12] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[13] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            predicates[14] = criteriaBuilder.equal(userJoin.get(User_.ID), SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[15] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE), userJoin.get(User_.NATIONAL_CODE));
            predicates[16] = criteriaBuilder.equal(unitJoin.get(Unit_.ID), modelMemberJoin.get(ModelMember_.MEMBER_ID));
            predicates[17] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.UNIT.getCode());


            if (!CollectionUtils.isEmpty(modelDto.getModelRoleCodeList())) {
                Join<ModelMemberRole, ModelRole> modelRoleJoin = subQueryRoot.join(ModelMemberRole_.MODEL_ROLE);
                Predicate modelRoleInPredicate = modelRoleJoin.get(ModelRole_.CODE).in(modelDto.getModelRoleCodeList());
                predicates = ArrayUtils.addAll(predicates, modelRoleInPredicate);
            }

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

    private Specification<Model> modelVersionPredicates(ModelDto modelDto, boolean filterByMininVersion) {
        return (root, query, criteriaBuilder) -> {
            Join<Model, ModelVersion> modelVersionJoin = root.join(Model_.MODEL_VERSIONS, JoinType.LEFT);
            Join<ModelVersion, CodeCategory> asisTobeJoin = modelVersionJoin.join(ModelVersion_.ASIS_TOBE);
            Predicate[] predicates;
            if (filterByMininVersion) {
                predicates = new Predicate[2];
                predicates[0] = criteriaBuilder.equal(asisTobeJoin.get(CodeCategory_.CODE), getProperAsisTobeCode(root, query, criteriaBuilder));
                predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            } else {
                predicates = new Predicate[1];
                predicates[0] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            }
            if (!CollectionUtils.isEmpty(modelDto.getStatusCodeList())) {
                Join<ModelVersion, CodeCategory> statusJoin = modelVersionJoin.join(ModelVersion_.STATUS);
                Predicate statusInPredicate = statusJoin.get(CodeCategory_.CODE).in(modelDto.getStatusCodeList());
                predicates = ArrayUtils.addAll(predicates, statusInPredicate);
            }
            return criteriaBuilder.and(predicates);
        };
    }

    private Subquery<String> getProperAsisTobeCode(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Subquery<String> subQuery = query.subquery(String.class);
        Root<ModelVersion> subQueryRoot = subQuery.from(ModelVersion.class);
        Join<ModelVersion, Model> modelJoin = subQueryRoot.join(ModelVersion_.MODEL);
        Join<ModelVersion, CodeCategory> asisTobeJoin = subQueryRoot.join(ModelVersion_.ASIS_TOBE);
        Expression maxExpr = criteriaBuilder.min(asisTobeJoin.get(CodeCategory_.CODE));
        subQuery.select(maxExpr);
        Predicate[] predicates = new Predicate[2];
        predicates[0] = criteriaBuilder.equal(modelJoin.get(Model_.ID), root.get(Model_.ID));
        predicates[1] = criteriaBuilder.equal(subQueryRoot.get(ModelVersion_.LAST_VERSION), true);
        subQuery.where(predicates);
        return subQuery;
    }

    private Specification<Model> userFavoriteExist() {
        return (root, query, criteriaBuilder) -> {
            Subquery<UserFavorite> subQuery = query.subquery(UserFavorite.class);
            Root<UserFavorite> subQueryRoot = subQuery.from(UserFavorite.class);
            Join<UserFavorite, User> userJoin = subQueryRoot.join(UserFavorite_.USER);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteriaBuilder.equal(userJoin.get(User_.ID), SecurityUtils.getCurrentUserOrElseThrow().getId());
            predicates[1] = criteriaBuilder.equal(subQueryRoot.get(UserFavorite_.REFERENCE_ID), root.get(Model_.ID));
            predicates[2] = criteriaBuilder.equal(subQueryRoot.get(UserFavorite_.REFERENCE_TYPE), UserFavorite.ReferenceType.MODEL.name());
            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

    @Transactional(readOnly = true)
    public SearchDto listSingleUserModels(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction) {

        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Page<Model> modelPage = null;
        List<ModelDto> content = new ArrayList<>();

        modelPage = modelRepository.listSingleUserModels(pageable, modelDto.getOwnerUserId(), SecurityUtils.getCurrentOrganIdOrElseThrow(), modelDto.getName());

        for (Model model : modelPage) {
            ModelDto newModelDto = mapToDto(model);
            ModelVersion lastAsisVersion = modelVersionService.getRepository()
                    .getLastVersion(ModelVersion.AsisTobe.AS_IS.getCode(), model.getId());

            if (lastAsisVersion != null) {
                newModelDto.setLastAsisVersion(modelVersionService.mapToDtoWithRefData(lastAsisVersion));
                List<ModelMemberRole> modelMemberRoleList = modelMemberRoleService.findByModelVersionIdAndUserIdAndStatus(lastAsisVersion.getId(),
                        modelDto.getOwnerUserId(), Status.ACTIVE.name());
                newModelDto.getLastAsisVersion().setModelMemberDtoList(modelMemberRoleService.mapToDto(modelMemberRoleList));
            }

            ModelVersion lastTobeVersion = modelVersionService.getRepository()
                    .getLastVersion(ModelVersion.AsisTobe.TO_BE.getCode(), model.getId());
            if (lastTobeVersion != null) {
                newModelDto.setLastTobeVersion(modelVersionService.mapToDtoWithRefData(lastTobeVersion));

                List<ModelMemberRole> modelMemberRoleList = modelMemberRoleService.findByModelVersionIdAndUserIdAndStatus(lastTobeVersion.getId(),
                        modelDto.getOwnerUserId(), Status.ACTIVE.name());

                newModelDto.getLastTobeVersion().setModelMemberDtoList(modelMemberRoleService.mapToDto(modelMemberRoleList));
            }
            content.add(newModelDto);
        }

        //content = modelPage.map(this::mapToDtoWithLastVersionData).getContent();
        for (ModelDto modelDt : content) {
            if(modelDt.getLastAsisVersion()!=null)
              modelDt.setAsisRole(getUserRolesOnModel(modelDto.getOwnerUserId(),modelDt.getLastAsisVersion().getModelMemberDtoList()));
            if(modelDt.getLastTobeVersion()!=null)
              modelDt.setTobeRole(getUserRolesOnModel(modelDto.getOwnerUserId(),modelDt.getLastTobeVersion().getModelMemberDtoList()));
        }
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page)
                .pageSize(size).build();
    }
    private String getUserRolesOnModel(Long userID,List<ModelMemberRoleDto> modelMemberRoleDtos){
        try {
            List<ModelRoleDto> asisRoleList=new ArrayList<>();

            for (ModelMemberRoleDto modelMemberRoleDto : modelMemberRoleDtos) {
                if (modelMemberRoleDto.getMemberType().equals(ModelMember.MemberType.USER.getCode())
                        && modelMemberRoleDto.getMemberId().equals(userID)) {
                    asisRoleList.add( modelMemberRoleDto.getModelRoleDto());
                    break;
                }else if(modelMemberRoleDto.getMemberType().equals(ModelMember.MemberType.POST.getCode()) ){
                    List<User> userList=  userService.findAllPostMember(modelMemberRoleDto.getMemberId());
                    if(userList.stream().anyMatch(user -> user.getId().equals(userID))){
                        asisRoleList.add( modelMemberRoleDto.getModelRoleDto());
                    }
                }else if(modelMemberRoleDto.getMemberType().equals(ModelMember.MemberType.JOB.getCode()) ){
                    List<User> userList=  userService.findAllJobMember(modelMemberRoleDto.getMemberId());
                    if(userList.stream().anyMatch(user -> user.getId().equals(userID))){
                        asisRoleList.add( modelMemberRoleDto.getModelRoleDto());
                    }
                }else if(modelMemberRoleDto.getMemberType().equals(ModelMember.MemberType.UNIT.getCode()) ){
                    List<User> userList=  userService.findAllUnitMember(modelMemberRoleDto.getMemberId());
                    if(userList.stream().anyMatch(user -> user.getId().equals(userID))){
                        asisRoleList.add( modelMemberRoleDto.getModelRoleDto());
                    }
                }
               return  asisRoleList.stream().map(ModelRoleDto::getName).distinct().collect(Collectors.joining(","));
            }
        } catch (Exception ex) {
            return "-";
        }
        return "-";
    }
    @Override
    public Integer countModelByOrganId(Long id) {
        return modelRepository.getOrganModelCount(id);
    }


    @Transactional(readOnly = true)
    public SearchDto listModelDecisions(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Model>> optionalSpecification = createSpecification(modelDto, true);
        Page<Model> modelPage = findAll(optionalSpecification.get(), pageable);
        List<ModelDto> content = modelPage.map(this::mapToDtoWithLastVersionData).getContent();

        for (ModelDto modelDt : content) {
            if (modelDt.getLastAsisVersion() != null) {
                long modelVersionId = modelDt.getLastAsisVersion().getId();
                FilesDto xmlFile = modelVersionService.getXml(modelVersionId);
                BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());
                modelDt.setBpmnReportDtoList(bpmnHelperService.getModelGatewaysWithLane());
            }
        }

        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page)
                .pageSize(size).build();

    }


}
