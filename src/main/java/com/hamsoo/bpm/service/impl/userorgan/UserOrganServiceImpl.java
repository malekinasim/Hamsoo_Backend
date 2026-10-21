package com.hamsoo.bpm.service.impl.userorgan;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.user.User_;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.userorgan.UserOrganRepository;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.person.PersonService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.json.JSONObject;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserOrganServiceImpl implements UserOrganService {

    public UserOrganRepository userOrganRepository;
    public OrganService organService;
    public UserService userService;
    public PersonService personService;
    public ModelMemberService modelMemberService;
    public ModelMemberRoleService modelMemberRoleService;
    public ModelApproverService modelApproverService;
    public NotificationService notificationService;
    public OrganPlanService organPlanService;
    private final RoleMemberService roleMemberService;
    private final RoleService roleService;

    public UserOrganServiceImpl(UserOrganRepository userOrganRepository, OrganService organService,
                                UserService userService,
                                NotificationService notificationService,
                                ModelMemberService modelMemberService,
                                @Lazy PersonService personService,
                                ModelApproverService modelApproverService,
                                ModelMemberRoleService modelMemberRoleService,
                                OrganPlanService organPlanService,
                                RoleMemberService roleMemberService, RoleService roleService) {
        this.userOrganRepository = userOrganRepository;
        this.organService = organService;
        this.userService = userService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.modelMemberService = modelMemberService;
        this.modelApproverService = modelApproverService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.roleMemberService = roleMemberService;
        this.roleService = roleService;
        this.organPlanService=organPlanService;
    }

    @Override
    public UserOrganRepository getRepository() {
        return userOrganRepository;
    }

    @Override
    public List<UserDto> findAllUserOfOrgan() {
        return this.findAllUserOfOrgan(SecurityUtils.getCurrentOrganIdOrElseThrow()).stream().map(UserOrganDto::getUserDto).toList();

    }

    @Override
    public List<UserOrganDto> findAllUserOfOrgan(Long Id) {
        return getRepository().findALLByOrgan(Id, Status.ACTIVE.name()).stream().map(this::mapToDto).toList();
    }

    @Override
    public UserOrgan findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.userOrgan.idIsNotValid");
        }
        return getRepository().findByIdAndStatus(id, Status.ACTIVE.name()).orElseThrow(() -> new BusinessException("error.userOrgan.idIsNotValid"));
    }

    @Override
    public Set<UserOrgan> findAllOrganOfUser(User user) {
        return userOrganRepository.findAllByUser(user);
    }


    @Override
    @Transactional(readOnly = true)
    public UserOrganDto mapToDto(UserOrgan userOrgan) {
        Assert.notNull(userOrgan, "userOrgan is null");
        userOrgan = findByIdOrElseThrow(userOrgan.getId());
        UserOrganDto userOrganDto = new UserOrganDto();
        userOrganDto.setId(userOrgan.getId());
        userOrganDto.setOrganId(userOrgan.getOrgan().getId());
        userOrganDto.setUserId(userOrgan.getUser().getId());
        userOrganDto.setUserDto(userService.mapToDto(userOrgan.getUser()));

        userOrganDto.setOrganDto(organService.mapToDto(userOrgan.getOrgan()));
        Optional<RoleMember> roleMember = roleMemberService.findAllByUserAndOrgan(userOrgan.getUser().getId(), userOrgan.getOrgan().getId()).stream().findFirst();

        if (!roleMember.isEmpty())
            userOrganDto.setRoleDto(roleService.mapToDto(roleMember.get().getRole()));
        //userService.mapToDto(userOrgan.get()));
        userOrganDto.setUserModelCount(modelMemberService.getCountAllLastVersionByUserId(userOrgan.getUser().getId()));

        return userOrganDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserOrgan getOrElseThrow(Long userId, Long organId) {
        Assert.notNull(userId, "userId is null");
        Assert.notNull(organId, "organId id is null");
        UserOrgan userOrgan = getRepository().findByUserAndOrgan(userId, organId, Status.ACTIVE.name());
        if (userOrgan == null) {
            throw new BusinessException("error.modelManagement.userNotFoundInOrgan");
        }
        return userOrgan;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserOrgan> findAllByUserAndOrgan(Long userId, Long organId) {
        Assert.notNull(userId, "userId is null");
        Assert.notNull(organId, "organId id is null");
        return getRepository().findAllByUserAndOrgan(userId, organId, Status.ACTIVE.name());

    }

    @Override
    public OrganDto getCurUserOrgan(String curOrgan, String username) {
        Optional<Organ> org = Optional.empty();
        if (curOrgan != null && !curOrgan.equals("null")) {
            org = organService.findByIdAndUserName(curOrgan, username);
        }
        if (org.isEmpty()) {
            org = organService.getDefualtUserOrgan(username);
        }
        if (org.isPresent())
            return organService.mapToDto(org.get());
        return null;
    }

    @Override
    public UserOrgan getUserPrivateOrganObj(Account account, User user) {
        //add user to this workspace
        Organ myWorkspace = organService.getUserPrivateOrganObj(account);
        UserOrgan userOrgan = new UserOrgan();
        userOrgan.setOrgan(myWorkspace);
        userOrgan.setUser(user);
        userOrgan.setaDefault(true);
        userOrgan.setCreatedBy(user);
        userOrgan.setModifiedBy(user);
        userOrgan = save(userOrgan);


        RoleMemberDto roleMemberDto = new RoleMemberDto();
        roleMemberDto.setOrganId(userOrgan.getOrgan().getId());
        roleMemberDto.setUserId(userOrgan.getUser().getId());
        roleMemberDto.setRoleId(roleService.getUserRole().getId());

        roleMemberService.create(roleMemberDto);
        return userOrgan;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<UserOrgan> createAll(List<UserOrganDto> userOrganDto) {
        List<UserOrgan> userOrgans = userOrganDto.stream().map(this::mapToEntity).toList();
        return saveAll(userOrgans);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserOrgan create(UserOrganDto userOrganDto) {
       organPlanService.CheckOrganUserCountLimitBaseOnLastOrganPlan(userOrganDto.getOrganId());
        UserOrgan userOrgan = mapToEntity(userOrganDto);
        UserOrgan userOrg = save(userOrgan);
        if (userOrganDto.getRoleId() == null) {
            userOrganDto.setRoleId(roleService.getUserRole().getId());
        }
        RoleMemberDto roleMemberDto = new RoleMemberDto();
        roleMemberDto.setOrganId(userOrganDto.getOrganId());
        roleMemberDto.setUserId(userOrganDto.getUserId());
        roleMemberDto.setRoleId(userOrganDto.getRoleId());

        roleMemberService.create(roleMemberDto);
        return userOrg;

    }

    private UserOrgan mapToEntity(UserOrganDto userOrganDto) {
        Assert.notNull(userOrganDto, "userOrganDto is null");
        Assert.notNull(userOrganDto.getOrganId(), "organId id is null");
        Assert.notNull(userOrganDto.getUserId(), "userId id is null");
        UserOrgan userOrgan = new UserOrgan();
        User user = userService.findByIdOrElseThrow(userOrganDto.getUserId());
        Organ organ = organService.findByIdOrElseThrow(userOrganDto.getOrganId());
        if (userOrganDto.getPersonId() != null) {
            Person person = personService.findByIdOrElseThrow(userOrganDto.getPersonId());
            userOrgan.setPerson(person);
        }

        userOrgan.setOrgan(organ);
        userOrgan.setFromDate(LocalDateTime.now());
        userOrgan.setUser(user);
        userOrgan.setaDefault(false);

        userOrgan.setCreatedBy(user);
        userOrgan.setModifiedBy(user);

        return userOrgan;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveOrganMemberAndModelMemberOfOrgan(Long id) {
        UserOrgan userOrgan = findByIdOrElseThrow(id);

        //get  model of this user in this  organ
        List<ModelMemberRole> modelMemberRoles = modelMemberRoleService.findByUserIdAndStatusAndOrganId(userOrgan.getUser().getId(), Status.ACTIVE.name(), id);


        //if user is admin of this organ cannot delete or passive this user
        if (userOrgan.getUser().getRoleMembers().stream()
                .anyMatch(roleMember ->
                        roleMember.getStatus().equals(Status.ACTIVE.name()) &&
                                roleMember.getStatus().equals(Status.ACTIVE.name()) &&
                                roleMember.getRole().getName().equals(Role.OrganRole.ADMIN_ROLE.getCode()) &&
                                roleMember.getOrgan() != null &&
                                roleMember.getOrgan() != null &&
                                roleMember.getOrgan().getId().equals(userOrgan.getOrgan().getId())

                )) {
            List<RoleMember> roleMembers = new ArrayList<>();
            findByOrgan(userOrgan.getOrgan().getId()).stream()
                    .map(UserOrgan::getUser)
                    .filter(user -> !user.getId().equals(userOrgan.getId())).forEach(user ->
                            {
                                roleMembers.addAll(
                                        user.getRoleMembers().stream()
                                                .filter(roleMember ->
                                                        roleMember.getStatus().equals(Status.ACTIVE.name()) &&
                                                                roleMember.getRole().getName().equals(Role.OrganRole.ADMIN_ROLE.getCode()) &&
                                                                roleMember.getOrgan() != null &&
                                                                roleMember.getOrgan().getId().equals(userOrgan.getOrgan().getId())).toList());

                            }
                    );

            if (roleMembers.size() == 0)
                throw new BusinessException("error.userOrgan.cannotDelete.isAdmin", userOrgan.getOrgan().getOrganType().equals(Organ.OrganType.ORGAN) ? userOrgan.getOrgan().getDescription() : userOrgan.getUser().getFullName());
        }else{
            //passive all  role_member for this user in this  organ
            roleMemberService.passiveAll(userOrgan.getUser().getRoleMembers());
        }

        //if user is owner of model in this organ cannot delete user_organ
        if (modelMemberRoles.stream().anyMatch(mem -> mem.getModelRole().getCode().equals(ModelRole.Code.MODEL_OWNER.getCode())))
            throw new BusinessException("error.userOrgan.cannotDelete.hasModelAsOwner");

        //passive all  model_member_role except owner Organ of this user in this  organ
        modelMemberRoles.forEach(modelMemberRole -> modelMemberRoleService.passive(modelMemberRole.getId()));


        //get of model that send to  this user for approve or FYI
        List<ModelApprover> curUserModelApprovers = new ArrayList<>();

        modelMemberRoles.stream()
                .filter(modelMember -> modelMember.getModelApprovers() != null && !
                        modelMember.getModelApprovers().isEmpty())
                .map(ModelMemberRole::getModelApprovers).forEach(modelApprovers ->
                        curUserModelApprovers.addAll(modelApprovers.stream().filter(modelApprover ->
                                modelApprover.getStatus().equals(Status.ACTIVE.name()) &&
                                        (modelApprover.getApproverLogs() == null ||
                                                modelApprover.getApproverLogs().isEmpty())).toList()));


        //get of model that send to  this user for approve or FYI Modelstate is FORAPPROVE
        List<ModelApprover> approveModelApprovers = curUserModelApprovers.stream()
                .filter(modelApprover ->
                        modelApprover.getModelMemberRole().getModelMember().getModelVersion().getStatus().getCode().equals(ModelVersion.Status.FOR_APPROVE.getCode())
                ).toList();


        //if user is approver of model in this organ cannot delete user_organ
        if (!approveModelApprovers.isEmpty())
            throw new BusinessException("error.userOrgan.cannotDelete.hasModelAsApprove");


        //passive all approve for user that is draft
        List<Long> draftmodelApprovers = curUserModelApprovers.stream()
                .filter(modelApprover ->
                        modelApprover.getModelMemberRole().getModelMember().getModelVersion().getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())
                ).map(ModelApprover::getId).toList();

        modelApproverService.passiveAll(draftmodelApprovers);


        //passive user in organ
//        String notificationText = ResourceBundleUtils.getMessage("notify.userOrgan.userLeftOrgan",
//                userOrgan.getUser().getFullName(), userOrgan.getOrgan().getName());
        //  createNotification(userOrgan, notificationText);





        userOrgan.setStatus(Status.PASSIVE.name());
        userOrgan.setToDate(LocalDateTime.now());
        save(userOrgan);

    }

    private List<UserOrgan> findByOrgan(Long organId) {
        return this.getRepository().findALLByOrgan(organId, Status.ACTIVE.name());
    }


    private void createNotification(UserOrgan userOrgan, String notificationText) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotification(notificationText);
        notificationDto.setRefId(userOrgan.getOrgan().getId());
        notificationDto.setEntityType(Notification.EntityType.ORGAN.getValue());
        NotificationAudienceDto notificationAudienceDto = new NotificationAudienceDto(userOrgan.getUser().getId());
        List<NotificationAudienceDto> notificationAudienceDtoList = new ArrayList<>();
        notificationAudienceDtoList.add(notificationAudienceDto);
        notificationDto.setNotificationAudienceDtoList(notificationAudienceDtoList);

        JSONObject notificationParamValues = new JSONObject();
        notificationParamValues.put("organId", userOrgan.getOrgan().getId());
        notificationDto.setNotificationParamValues(notificationParamValues);
        notificationService.validateAndCreate(notificationDto);
    }


    @Override
    public SearchDto list(Long organId, int page, int size, String filter, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<UserOrgan>> optionalSpecification = createSpecification(organId, filter);
        Page<UserOrgan> userOrganPage = findAll(optionalSpecification.get(), pageable);
        List<UserOrganDto> content = userOrganPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(userOrganPage.getTotalElements())
                .totalPages(userOrganPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public List<UserOrgan> findAllByPersonId(Long personId) {
        return this.getRepository().findAllByPersonId(personId, Status.ACTIVE.name());
    }

    @Override
    public UserOrgan edit(UserOrganDto userOrganDto) {
        Assert.notNull(userOrganDto, "userOrganDto is null");
        Assert.notNull(userOrganDto.getOrganId(), "organId id is null");
        Assert.notNull(userOrganDto.getUserId(), "userId id is null");
        UserOrgan userOrgan = this.findByIdOrElseThrow(userOrganDto.getId());
        User user = userService.findByIdOrElseThrow(userOrganDto.getUserId());
        Organ organ = organService.findByIdOrElseThrow(userOrganDto.getOrganId());
        if (userOrganDto.getPersonId() != null) {
            Person person = personService.findByIdOrElseThrow(userOrganDto.getPersonId());
            userOrgan.setPerson(person);
        }
        userOrgan.setOrgan(organ);
        userOrgan.setUser(user);

        return save(userOrgan);
    }

    @Override
    public void passiveAllOrganMemberAndModelMemberOfOrgan(List<UserOrganDto> passiveUserOrganDtos) {
        passiveUserOrganDtos.stream().map(UserOrganDto::getId).forEach(
                this::passiveOrganMemberAndModelMemberOfOrgan
        );
    }

    @Override
    public List<User> getAdminUsersOfOrgan(Long organId) {
        return userOrganRepository.findMemberOfOrganBySpecificRole(organId, Status.ACTIVE.name(), Role.OrganRole.ADMIN_ROLE.getCode());
    }

    @Override
    public Integer countUserOrganByOrganId(Long organId) {
        return userOrganRepository.CountUserOrganByOrganIdByLock(organId);
    }


    private Optional<Specification<UserOrgan>> createSpecification(Long organId, String search) {
        List<Specification<UserOrgan>> specificationList = new ArrayList<>();
        if (StringUtils.hasText(search) && !search.equals("null")) {
            specificationList.add((root, query, criteriaBuilder) -> {
                Join<UserOrgan, User> userJoin = root.join(UserOrgan_.USER);
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get(User_.fullName)),
                        "%" + search.toLowerCase() + "%"));
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get(User_.nationalCode)),
                        "%" + search.toLowerCase() + "%"));
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            });
        }

        Specification<UserOrgan> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(UserOrgan_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);
        Specification<UserOrgan> organEq = (root, query, criteriaBuilder) -> {
            Join<UserOrgan, Organ> organJoin = root.join(UserOrgan_.ORGAN);
            Predicate[] predicates = new Predicate[2];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), organId);
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            return criteriaBuilder.and(predicates);
        };

        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }


}
