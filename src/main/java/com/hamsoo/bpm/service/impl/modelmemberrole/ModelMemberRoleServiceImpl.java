package com.hamsoo.bpm.service.impl.modelmemberrole;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberUserDto;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberUserDtoImpl;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.user.UserService;
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
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.person.Person_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.post.Post_;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.postmember.PostMember_;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.unit.Unit_;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.user.User_;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelmemberrole.ModelMemberRoleRepository;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelMemberRoleServiceImpl implements ModelMemberRoleService {
    private final ModelMemberRoleRepository modelMemberRoleRepository;
    private final ModelMemberService modelMemberService;
    private final ModelVersionService modelVersionService;
    private final ModelRoleService modelRoleService;
    private final NotificationService notificationService;
    private final UserService userService;

    public ModelMemberRoleServiceImpl(ModelMemberRoleRepository modelMemberRoleRepository,
                                      @Lazy ModelMemberService modelMemberService,
                                      ModelVersionService modelVersionService,
                                      ModelRoleService modelRoleService,
                                      NotificationService notificationService, UserService userService) {
        this.modelMemberRoleRepository = modelMemberRoleRepository;
        this.modelMemberService = modelMemberService;
        this.modelVersionService = modelVersionService;
        this.modelRoleService = modelRoleService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public ModelMemberRoleRepository getRepository() {
        return modelMemberRoleRepository;
    }

    @Override
    public ModelMemberRole findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelMemberRole.idIsNotValid");
        }
        return getRepository().findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.modelMemberRole.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelMemberRole passive(Long id) {
        ModelMemberRole modelMemberRole = findByIdOrElseThrow(id);
        if (modelMemberRole.getModelRole().getCode().equals(ModelRole.Code.MODEL_OWNER))
            throw new BusinessException("error.modelMember.modelOwnerNotPassiveValidator");

        modelMemberRole.setToDate(LocalDateTime.now());
        modelMemberRole.setStatus(Status.PASSIVE.name());
        save(modelMemberRole);
        List<ModelMemberRole> modelMemberRoleList = this.findByModelMemberId(modelMemberRole.getModelMember().getId());
        //this is last model member role then passive role
        if (modelMemberRoleList.size() == 0) {
            modelMemberService.passiveAndDeleteModelFavorite(modelMemberRole.getModelMember().getId());
        }

        List<User> userList = new ArrayList<>();
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.USER.getCode())) {
            Optional<User> userOptional = userService.findById(modelMemberRole.getModelMember().getMemberId());
            if (userOptional.isPresent()) {
                userList.add(userOptional.get());
            }
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.POST.getCode())) {
            userList.addAll( userService.findAllPostMember(modelMemberRole.getModelMember().getMemberId()));
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.JOB.getCode())) {
            userList.addAll( userService.findAllJobMember(modelMemberRole.getModelMember().getMemberId()));
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.UNIT.getCode())) {
            userList.addAll( userService.findAllUnitMember(modelMemberRole.getModelMember().getMemberId()));
        }

        userList.stream().distinct().forEach(user -> {
                    String notificationText = ResourceBundleUtils.getMessage("info.modelMember.userLeftModel",
                            modelMemberRole.getModelRole().getName(), user.getFullName(),
                            modelMemberRole.getModelMember().getModelVersion().getModel().getName());
                    createNotification(modelMemberRole.getModelMember().getModelVersion().getId(), notificationText);
                }
        );

        return modelMemberRole;

    }

    @Override
    public List<ModelMemberRole> findByModelMemberId(Long modelMemberId) {
        return getRepository().findByModelMemberId(modelMemberId, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public SearchDto list(ModelMemberRoleDto modelMemberRoleDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);

        ModelVersion lastModelVersion = modelVersionService.getLastVersion(modelMemberRoleDto.getModelVersionId());
        modelMemberRoleDto.setModelVersionId(lastModelVersion.getId());

        Optional<Specification<ModelMemberRole>> optionalSpecification = createSpecification(modelMemberRoleDto);
        Page<ModelMemberRole> modelMemberRolePage = findAll(optionalSpecification.get(), pageable);
        List<ModelMemberRoleDto> content = modelMemberRolePage.map(this::mapToDtoByOnlineOfflineMode).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelMemberRolePage.getTotalElements())
                .totalPages(modelMemberRolePage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }
    @Override
    public List<ModelMemberUserDtoImpl> userList(Long modelVersionId) {
       List<ModelMemberUserDto> modelMemberUserDtos=  this.getRepository().FindAllModelUserMember(modelVersionId,SecurityUtils.getCurrentOrganIdOrElseThrow());
       List<UserDto> onlinemem= modelMemberService.findAllOnlineUserOfModelVersion(modelVersionId);
       return modelMemberUserDtos.stream().map( mem-> {
            ModelMemberUserDtoImpl modelMemberUserDto=new ModelMemberUserDtoImpl(mem.getName(),mem.getFamily(),
                    mem.getUserId(),mem.getNationalCode(),mem.getImageId(),mem.getRoles(),mem.getRoleNames(),mem.getMembers(),false);
            if(onlinemem.stream().anyMatch(onmem-> onmem.getId().equals(mem.getUserId())))
                modelMemberUserDto.setOnline(true);
            else     modelMemberUserDto.setOnline(false);
            return modelMemberUserDto;
        } ).toList();

    }

    private Optional<Specification<ModelMemberRole>> createSpecification(ModelMemberRoleDto modelMemberRoleDto) {

        List<Specification<ModelMemberRole>> specificationList = new ArrayList<>();

        Specification<ModelMemberRole> organEq = (root, query, criteriaBuilder) -> {
            Join<ModelMemberRole, ModelMember> modelMemberJoin = root.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Predicate[] predicates = new Predicate[6];
            predicates[0] = criteriaBuilder.equal(root.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.STATUS), Status.ACTIVE.name());
            predicates[2] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[3] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelMemberRoleDto.getModelVersionId());
            predicates[4] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());
            predicates[5] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.LAST_VERSION), true);
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);

        if (!CollectionUtils.isEmpty(modelMemberRoleDto.getModelRoleCodeList())) {
            Specification<ModelMemberRole> modelRoleIn = (root, query, criteriaBuilder) -> {
                Join<ModelMemberRole, ModelRole> modelRoleJoin = root.join(ModelMemberRole_.MODEL_ROLE);
                Predicate modelRoleInPredicate = modelRoleJoin.get(ModelRole_.CODE).in(modelMemberRoleDto.getModelRoleCodeList());
                return criteriaBuilder.and(modelRoleInPredicate);
            };
            specificationList.add(modelRoleIn);
        }

//        Optional<Specification<ModelMemberRole>> modelMemSpecification= modelMemberRoleExist( modelMemberRoleDto.getModelVersionId());
//        if(modelMemSpecification.isPresent())
//            specificationList.add(modelMemSpecification.get());

        return SpecificationUtils.andSpecifications(specificationList);

    }

    private ModelMemberRoleDto baseMapapToDo(ModelMemberRole modelMemberRole) {
        ModelMemberRoleDto modelMemberRoleDto = new ModelMemberRoleDto();
        modelMemberRoleDto.setId(modelMemberRole.getId());
        modelMemberRoleDto.setFromDate(modelMemberRole.getFromDate());
        modelMemberRoleDto.setToDate(modelMemberRole.getToDate());
        modelMemberRoleDto.setModelRoleId(modelMemberRole.getModelRole().getId());
         modelMemberRoleDto.setMemberId(modelMemberRole.getModelMember().getMemberId());
         modelMemberRoleDto.setMemberType(modelMemberRole.getModelMember().getMemberType().getCode());
        modelMemberRoleDto.setModelVersionId(modelMemberRole.getModelMember().getModelVersion().getId());
        modelMemberRoleDto.setModelRoleDto(modelRoleService.mapToDto(modelMemberRole.getModelRole()));
        return modelMemberRoleDto;
    }

    @Override
    public ModelMemberRoleDto mapToDto(ModelMemberRole modelMemberRole) {
        ModelMemberRoleDto modelMemberRoleDto = baseMapapToDo(modelMemberRole);
        modelMemberRoleDto.setModelMemberDto(modelMemberService.mapToDto(modelMemberRole.getModelMember()));

        return modelMemberRoleDto;
    }

    @Override
    public ModelMemberRoleDto mapToDtoByOnlineOfflineMode(ModelMemberRole modelMemberRole) {
        ModelMemberRoleDto modelMemberRoleDto = baseMapapToDo(modelMemberRole);
        modelMemberRoleDto.setModelMemberDto(modelMemberService.mapToDtoByOnlineInfo(modelMemberRole.getModelMember()));
        return modelMemberRoleDto;
    }

    @Override
    public List<ModelMemberRoleDto> mapToDto(List<ModelMemberRole> modelMemberRoles) {
        return modelMemberRoles.stream().map(modelMemberRole -> mapToDto(modelMemberRole)).toList();
    }

    @Override
    public ModelMemberRole create(ModelMemberRoleDto modelMemberRoleDto) {
        Assert.notNull(modelMemberRoleDto, "modelMemberRoleDto is null");
        ModelRole modelRole = modelRoleService.findByIdOrElseThrow(modelMemberRoleDto.getModelRoleId());
        if (!modelRole.getCode().equals(ModelRole.Code.MODEL_APPROVER.getCode()))
            modelVersionService.checkModelVersionStatus(modelMemberRoleDto.getModelVersionId());

        Optional<ModelMember> modelMemberOption = getRepository().findByModelVersionIdAndOrganAndMemberIdAndMemberType(
                modelMemberRoleDto.getModelVersionId(),
                modelMemberRoleDto.getMemberId(),
                modelMemberRoleDto.getMemberType(),
                Status.ACTIVE.name(),
                SecurityUtils.getCurrentOrganIdOrElseThrow());

        ModelMember modelMember;

        if (modelMemberOption.isEmpty()) {
            ModelMemberDto modelMemberDto = new ModelMemberDto();
            modelMemberDto.setMemberType(modelMemberRoleDto.getMemberType());
            modelMemberDto.setMemberId(modelMemberRoleDto.getMemberId());
            modelMemberDto.setModelVersionId(modelMemberRoleDto.getModelVersionId());
            modelMember = modelMemberService.create(modelMemberDto);

        } else modelMember = modelMemberOption.get();

        ModelMemberRole modelMemberRole = new ModelMemberRole();
        modelMemberRole.setModelMember(modelMember);
        modelMemberRole.setFromDate(LocalDateTime.now());
        modelMemberRole.setStatus(Status.ACTIVE.name());
        modelMemberRole.setModelMember(modelMember);
        modelMemberRole.setModelRole(modelRole);
        this.save(modelMemberRole);


        List<User> userList=new ArrayList<>();
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.USER.getCode())) {
            Optional<User> optionalUser= userService.findById(modelMemberRole.getModelMember().getMemberId());
            if(optionalUser.isPresent())
                  userList.add(optionalUser.get());
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.POST.getCode())) {
            userList.addAll( userService.findAllPostMember(modelMemberRole.getModelMember().getMemberId()));
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.JOB.getCode())) {
            userList.addAll( userService.findAllJobMember(modelMemberRole.getModelMember().getMemberId()));
        }
        if (modelMemberRole.getModelMember().getMemberType().getCode().equals(ModelMember.MemberType.UNIT.getCode())) {
            userList.addAll( userService.findAllUnitMember(modelMemberRole.getModelMember().getMemberId()));
        }

        userList.stream().distinct().forEach(user -> {
                    String notificationText = ResourceBundleUtils.getMessage("info.modelMember.userJoinModel",
                            modelMemberRole.getModelRole().getName(), user.getFullName(),
                            modelMemberRole.getModelMember().getModelVersion().getModel().getName());
                    createNotification(modelMemberRole.getModelMember().getModelVersion().getId(), notificationText);
                }
        );


        return modelMemberRole;

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelMemberRole edit(ModelMemberRoleDto modelMemberRoleDto) {
        Assert.notNull(modelMemberRoleDto, "modelMemberRoleDto is null");
        ModelMemberRole modelMemberRole = findByIdOrElseThrow(modelMemberRoleDto.getId());
        modelVersionService.checkModelVersionStatus(modelMemberRole.getModelMember().getModelVersion().getId());
        this.passive(modelMemberRole.getId());
        return create(modelMemberRoleDto);
    }

    @Override
    public List<ModelMemberRoleDto> curUserModelRoleList(Long modelVersionId) {
        Optional<Specification<ModelMemberRole>> optionalSpecification = createSpecification(modelVersionId);
        List<ModelMemberRole> modelMemberRolePage = findAll(optionalSpecification.get());
        List<ModelMemberRoleDto> content = modelMemberRolePage.stream().map(this::mapToDto).collect(Collectors.toList());
        return content;
    }

    @Override
    public List<ModelMemberRole> findByModelVersionIdAndUserIdAndStatus(Long modelVersionId, Long userId, String status) {
        return getRepository().findByModelVersionIdAndUserIdAndStatus(modelVersionId, userId, SecurityUtils.getCurrentOrganIdOrElseThrow(), status);
    }

    @Override
    public List<ModelMemberRole> findByModelVersionIdAndMemberIdAndMemberTypeAndRoleIdAndStatus(Long modelVersionId, Long memberId, String memberType, Long modelRoleId, String status) {
        return getRepository().findByModelVersionIdAndUserIdAndRoleIdAndStatus(modelVersionId, memberId, memberType, SecurityUtils.getCurrentOrganIdOrElseThrow(), modelRoleId, status);
    }

    @Override
    public List<ModelMemberRole> findByUserIdAndStatusAndOrganId(Long userId, String status, Long organId) {
        return getRepository().findByUserIdAndStatusAndOrganId(userId, status, organId);
    }

    @Override
    public ModelMemberRole changeOwner(ModelMemberRoleDto modelMemberRoleDto) {
        modelVersionService.checkModelVersionStatus(modelMemberRoleDto.getModelVersionId());
        ModelRole ownerRole = modelRoleService.findByCodeOrElseThrow(ModelRole.Code.MODEL_OWNER.getCode());
        List<ModelMemberRole> modelMemberRoleList = getRepository().findByModelVersionIdAndRoleIdAndStatus(
                modelMemberRoleDto.getModelVersionId(),
                SecurityUtils.getCurrentOrganIdOrElseThrow(),
                ownerRole.getId(),
                Status.ACTIVE.name()
        );
        modelMemberRoleList.forEach(modelMemberRole -> passive(modelMemberRole.getId()));
        return create(modelMemberRoleDto);
    }


    private Optional<Specification<ModelMemberRole>> createSpecification(Long modelVersionId) {
        List<Specification<ModelMemberRole>> andSpecificationList = new ArrayList<>();

     Optional<Specification<ModelMemberRole>> modelMemSpecification= modelMemberRoleExist(modelVersionId);
        if(modelMemSpecification.isPresent())
              andSpecificationList.add(modelMemSpecification.get());

        return SpecificationUtils.andSpecifications(andSpecificationList);
    }

    private Optional<Specification<ModelMemberRole>> modelMemberRoleExist(Long modelVersionId) {
        List<Specification<ModelMemberRole>> specificationList = new ArrayList<>();
        specificationList.add(modelMemberRoleExistByUserMemberFilter(modelVersionId));
        specificationList.add(modelMemberRoleExistByPostMemberFilter(modelVersionId));
        specificationList.add(modelMemberRoleExistByJobMemberFilter(modelVersionId));
        specificationList.add(modelMemberRoleExistByUnitMemberFilter(modelVersionId));
        return SpecificationUtils.orSpecifications(specificationList);
    }

    private Specification<ModelMemberRole> modelMemberRoleExistByUserMemberFilter(Long modelVersionId) {
        return (root, query, criteriaBuilder) -> {

            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            subQuery.select(subQueryRoot);
            Predicate[] predicates = new Predicate[6];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.MEMBER_ID), SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[3] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.USER.getCode());
            predicates[4] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.ID), root.get(ModelMemberRole_.ID));
            predicates[5] =criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

    private Specification<ModelMemberRole> modelMemberRoleExistByPostMemberFilter(Long modelVersionId) {

        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);
            Join<Organ , Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit> unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Unit, Post> postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember> postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person> personJoin = postMemberJoin.join(PostMember_.PERSON);
            subQuery.select(subQueryRoot);

            Predicate[] predicates = new Predicate[14];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS),Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.MEMBER_ID), postJoin.get(Post_.ID));
            predicates[4] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.POST.getCode());

            predicates[5] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS),Status.ACTIVE.name());
            predicates[6] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS),Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(postJoin.get(Post_.STATUS),Status.ACTIVE.name());
            predicates[8] = criteriaBuilder.equal(postMemberJoin.get(PostMember_.STATUS),Status.ACTIVE.name());
            predicates[9] = criteriaBuilder.equal(personJoin.get(Person_.STATUS),Status.ACTIVE.name());
            predicates[10] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE),userJoin.get(User_.NATIONAL_CODE));
            predicates[11] = criteriaBuilder.equal(userJoin.get(User_.ID),SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[12] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.ID), root.get(ModelMemberRole_.ID));
            predicates[13] =criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());
            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

    private Specification<ModelMemberRole> modelMemberRoleExistByJobMemberFilter(Long modelVersionId) {

        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);
            Join<Organ , Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit> unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Chart, Job> jobJoin = chartJoin.joinList(Chart_.JOBS);
            Join<Unit, Post> postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember> postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person> personJoin = postMemberJoin.join(PostMember_.PERSON);
            subQuery.select(subQueryRoot);

            Predicate[] predicates = new Predicate[15];
            predicates[0]  = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1]  = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2]  = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS),Status.ACTIVE.name());
            predicates[3]  = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.MEMBER_ID), jobJoin.get(ModelMember_.ID));
            predicates[4]  = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.UNIT.getCode());
            predicates[5]  = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS),Status.ACTIVE.name());
            predicates[6]  = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS),Status.ACTIVE.name());
            predicates[7]  = criteriaBuilder.equal(postJoin.get(Post_.STATUS),Status.ACTIVE.name());
            predicates[8]  = criteriaBuilder.equal(jobJoin.get(Post_.STATUS),Status.ACTIVE.name());
            predicates[9]  = criteriaBuilder.equal(postMemberJoin.get(PostMember_.STATUS),Status.ACTIVE.name());
            predicates[10] = criteriaBuilder.equal(personJoin.get(Person_.STATUS),Status.ACTIVE.name());
            predicates[11] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE),userJoin.get(User_.NATIONAL_CODE));
            predicates[12] = criteriaBuilder.equal(userJoin.get(User_.ID),jobJoin.get(Job_.ID));
            predicates[13] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.ID), root.get(ModelMemberRole_.ID));
            predicates[14] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }

    private Specification<ModelMemberRole> modelMemberRoleExistByUnitMemberFilter(Long modelVersionId) {

        return (root, query, criteriaBuilder) -> {
            Subquery<ModelMemberRole> subQuery = query.subquery(ModelMemberRole.class);
            Root<ModelMemberRole> subQueryRoot = subQuery.from(ModelMemberRole.class);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = subQueryRoot.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, CodeCategory> memberTypeJoin = modelMemberJoin.join(ModelMember_.MEMBER_TYPE);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<Organ, UserOrgan> userOrganJoin = organJoin.joinList(Organ_.USER_ORGANS);
            Join<UserOrgan, User> userJoin = userOrganJoin.join(UserOrgan_.USER);
            Join<Organ , Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Join<Chart, Unit> unitJoin = chartJoin.joinList(Chart_.UNITS);
            Join<Unit, Post> postJoin = unitJoin.joinList(Unit_.POSTS);
            Join<Post, PostMember> postMemberJoin = postJoin.joinList(Post_.POST_MEMBERS);
            Join<PostMember, Person> personJoin = postMemberJoin.join(PostMember_.PERSON);
            subQuery.select(subQueryRoot);

            Predicate[] predicates = new Predicate[14];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS),Status.ACTIVE.name());
            predicates[3] = criteriaBuilder.equal(modelMemberJoin.get(ModelMember_.MEMBER_ID), unitJoin.get(Unit_.ID));
            predicates[4] = criteriaBuilder.equal(memberTypeJoin.get(CodeCategory_.CODE), ModelMember.MemberType.UNIT.getCode());

            predicates[5] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS),Status.ACTIVE.name());
            predicates[6] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS),Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(postJoin.get(Post_.STATUS),Status.ACTIVE.name());
            predicates[8] = criteriaBuilder.equal(postMemberJoin.get(PostMember_.STATUS),Status.ACTIVE.name());
            predicates[9] = criteriaBuilder.equal(personJoin.get(Person_.STATUS),Status.ACTIVE.name());

            predicates[10] = criteriaBuilder.equal(personJoin.get(Person_.NATIONAL_CODE),userJoin.get(User_.NATIONAL_CODE));
            predicates[11] = criteriaBuilder.equal(userJoin.get(User_.ID),SecurityUtils.getUserIdOfCurrentUserOrElseThrow());
            predicates[12] = criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.ID), root.get(ModelMemberRole_.ID));
            predicates[13] =criteriaBuilder.equal(subQueryRoot.get(ModelMemberRole_.STATUS), Status.ACTIVE.name());

            subQuery.where(predicates);
            return criteriaBuilder.exists(subQuery);
        };
    }




    private void createNotification(Long modelVersionId, String notificationText) {
        List<User> members = modelMemberService.getMembersOfModel(modelVersionId);
        members.remove(SecurityUtils.getCurrentUserOrElseThrow());
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotification(notificationText);
        notificationDto.setRefId(modelVersionId);
        notificationDto.setEntityType(Notification.EntityType.MODEL_VERSION.getValue());
        notificationDto.setNotificationApiAddressCode(Notification.ApiAddressCode.EDIT_MODEL_API.getValue());

        List<NotificationAudienceDto> notificationAudienceDtoList = members.stream()
                .map(user -> new NotificationAudienceDto(user.getId())).toList();
        notificationDto.setNotificationAudienceDtoList(notificationAudienceDtoList);

        JSONObject notificationParamValues = new JSONObject();
        ModelVersion modelVersion = modelVersionService.getById(modelVersionId);
        notificationParamValues.put("modelId", modelVersion.getModel().getId());
        notificationParamValues.put("model_version_state", modelVersion.getAsisTobe().getCode());
        notificationDto.setNotificationParamValues(notificationParamValues);
        notificationService.validateAndCreate(notificationDto);
    }


}
