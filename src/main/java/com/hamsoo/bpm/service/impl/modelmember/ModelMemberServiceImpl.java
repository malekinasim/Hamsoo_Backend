package com.hamsoo.bpm.service.impl.modelmember;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelmember.ModelMemberRepository;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userfavorite.UserFavoriteService;
import com.hamsoo.bpm.service.impl.modelmemberrole.ModelMemberRoleServiceImpl;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT_PREFIX;

@Service
public class ModelMemberServiceImpl implements ModelMemberService {

    private final ModelMemberRepository modelMemberRepository;
    private final UserService userService;
    private final PostService postService;
    private final UnitService unitService;
    private final JobService jobService;
    private final ModelVersionService modelVersionService;
    private final UserFavoriteService userFavoriteService;
    private final ModelMemberRoleServiceImpl modelMemberRoleService;
    private final CodeCategoryService codeCategoryService;
    private final SimpUserRegistry simpUserRegistry;

    public ModelMemberServiceImpl(ModelMemberRepository modelMemberRepository,
                                  UserService userService, PostService postService,
                                  UnitService unitService, JobService jobService,
                                  ModelVersionService modelVersionService,
                                  UserFavoriteService userFavoriteService,
                                  ModelMemberRoleServiceImpl modelMemberRoleService,
                                  CodeCategoryService codeCategoryService,
                                  SimpUserRegistry simpUserRegistry) {
        this.modelMemberRepository = modelMemberRepository;
        this.userService = userService;
        this.postService = postService;
        this.unitService = unitService;
        this.jobService = jobService;
        this.modelVersionService = modelVersionService;
        this.userFavoriteService = userFavoriteService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.codeCategoryService = codeCategoryService;
        this.simpUserRegistry = simpUserRegistry;
    }

//    @PostConstruct
//    private void OffLineAllMembers() {
//        List<ModelMember> modelMembers = getRepository().findAllByMode(ModelMember.ModeType.ONLINE);
//        modelMembers.stream().forEach(modelMember -> modelMember.setMode(ModelMember.ModeType.OFFLINE));
//        getRepository().saveAll(modelMembers);
//    }

    @Override
    public ModelMemberRepository getRepository() {
        return modelMemberRepository;
    }

    @Override
    public ModelMember findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelMember.idIsNotValid");
        }
        return getRepository().findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(() -> new BusinessException("error.modelMember.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelMember create(ModelMemberDto modelMemberDto) {
        ModelMember modelMember = new ModelMember();
        modelMember.setFromDate(LocalDateTime.now());
        modelMember.setStatus(Status.ACTIVE.name());
        modelMember.setModelVersion(modelVersionService.findByIdOrElseThrow(modelMemberDto.getModelVersionId()));
        modelMember.setMemberId(modelMemberDto.getMemberId());
        modelMember.setMemberType(codeCategoryService.findByCategoryAndCode(ModelMember.MemberType.CATEGORY, modelMemberDto.getMemberType()));
        return save(modelMember);
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ModelMember passive(Long id) {
        ModelMember modelMember = findByIdOrElseThrow(id);
        modelMember.setToDate(LocalDateTime.now());
        modelMember.setStatus(Status.PASSIVE.name());
        modelMember = save(modelMember);

        List<ModelMemberRole> modelMemberRoleList = modelMemberRoleService.findByModelMemberId(id);
        modelMemberRoleList.forEach(modelMemberRole -> modelMemberRoleService.passive(modelMemberRole.getId()));
        return modelMember;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveAndDeleteModelFavorite(Long id) {
        ModelMember modelMember = passive(id);
        Long modelId = modelMember.getModelVersion().getModel().getId();
        Long memberId = modelMember.getMemberId();
        String memberType = modelMember.getMemberType().getCode();

        List<UserFavorite> userFavoriteList = new ArrayList<>();
        if (memberType.equals(ModelMember.MemberType.USER.getCode())) {
            userFavoriteList.addAll(userFavoriteService.findByReferenceTypeAndReferenceIdAndUserId(
                    UserFavorite.ReferenceType.MODEL.name(),
                    modelId, memberId));
        }
        if (memberType.equals(ModelMember.MemberType.POST.getCode()) ||
                memberType.equals(ModelMember.MemberType.JOB.getCode()) ||
                memberType.equals(ModelMember.MemberType.UNIT.getCode())) {

            List<User> userList = new ArrayList<>();
            if (memberType.equals(ModelMember.MemberType.POST.getCode())) {
                userList = userService.findAllPostMember(memberId);
            }
            if (memberType.equals(ModelMember.MemberType.JOB.getCode())) {
                userList = userService.findAllJobMember(memberId);
            }
            if (memberType.equals(ModelMember.MemberType.UNIT.getCode())) {
                userList = userService.findAllUnitMember(memberId);
            }
            userFavoriteList.addAll(userFavoriteService.findByReferenceTypeAndReferenceIdAndUserListId(UserFavorite.ReferenceType.MODEL.name(), modelId, userList.stream().map(User::getId).toList()));
        }
        for (UserFavorite userFavorite : userFavoriteList) {
            userFavoriteService.delete(userFavorite.getId(), false);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMemberDto mapToDto(ModelMember modelMember) {
        Assert.notNull(modelMember, "modelMember is null");
        modelMember = findByIdOrElseThrow(modelMember.getId());
        ModelMemberDto modelMemberDto = new ModelMemberDto();
        modelMemberDto.setId(modelMember.getId());
        modelMemberDto.setFromDate(modelMember.getFromDate());
        modelMemberDto.setToDate(modelMember.getToDate());
        modelMemberDto.setModelVersionId(modelMember.getModelVersion().getId());
        modelMemberDto.setMemberType(modelMember.getMemberType().getCode());
        if(modelMember.getMemberType().getCode().equals(ModelMember.MemberType.USER.getCode())) {

            modelMemberDto.setUserDto(userService.mapToDto(userService.findByIdOrElseThrow(modelMember.getMemberId())));
        }else if(modelMember.getMemberType().getCode().equals(ModelMember.MemberType.POST.getCode())){
            modelMemberDto.setPostDto(postService.mapToDto(postService.findByIdOrElseThrow(modelMember.getMemberId())));
        }else if(modelMember.getMemberType().getCode().equals(ModelMember.MemberType.JOB.getCode())){

            modelMemberDto.setJobDto(jobService.mapToDto(jobService.findByIdOrElseThrow(modelMember.getMemberId())));
        }else if(modelMember.getMemberType().getCode().equals(ModelMember.MemberType.UNIT.getCode())){

            modelMemberDto.setUnitDto(unitService.mapToDto(unitService.findByIdOrElseThrow(modelMember.getMemberId())));
        }
        return modelMemberDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ModelMemberDto mapToDtoByOnlineInfo(ModelMember modelMember) {
        Assert.notNull(modelMember, "modelMember is null");
        ModelMemberDto modelMemberDto = mapToDto(modelMember);
        List<UserDto> userDtos = findAllOnlineUserOfModelVersion(modelMember.getModelVersion().getId());
        if(modelMember.getMemberType().getCode().equals(ModelMember.MemberType.USER.getCode())) {
            modelMemberDto.setOnline(userDtos.stream().anyMatch(userDto -> userDto.getId().equals(modelMember.getMemberId())));
        }
        return modelMemberDto;
    }

    @Override
    public List<UserDto> findAllOnlineUserOfModelVersion(Long modelVersionId) {
        Set<SimpSubscription> matches = simpUserRegistry.findSubscriptions(new SimpSubscriptionMatcher() {
            @Override
            public boolean match(SimpSubscription subscription) {
                if (SecurityUtils.getCurrentUser().isPresent()) {
                    return subscription.getDestination().equals(TEAM_WORK_END_POINT_PREFIX + modelVersionId);
                }
                return false;
            }
        });
        return matches.stream().map(simpSubscription -> ((HamsooUserDetail) ((UsernamePasswordAuthenticationToken) (simpSubscription.getSession().getUser()).getPrincipal()).getPrincipal()).getUser()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMemberDto> mapToDto(List<ModelMember> modelMemberList) {
        if (CollectionUtils.isEmpty(modelMemberList)) {
            return new ArrayList<>();
        }
        return modelMemberList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId) {
        Assert.notNull(newModelVersionId, "newModelVersionId is null");
        Assert.notNull(oldModelVersionId, "oldModelVersionId is null");
        getRepository().updateModelVersionId(newModelVersionId, oldModelVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMember> findByModelVersionIdAndModelRoleCode(Long modelVersionId, String modelRoleCode) {
        return getRepository().findByModelVersionIdAndModelRoleCode(modelVersionId, modelRoleCode);
    }



    @Override
    @Transactional(readOnly = true)
    public List<ModelMember> findByModelIdAndModelRoleCodeAndUserId(Long modelId, String modelRoleCode, Long userId) {
        return getRepository().findByModelIdAndModelRoleCodeAndUserId(modelId, modelRoleCode, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMember> findByModelIdAndMemberIdAndMemberTypeId(Long modelId, Long memberId, Long memberTypeId) {
        return getRepository().findByModelIdAndMemberIdAndMemberType(modelId, memberId, memberTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelMember> getUserModelMember(Long userId, Long modelVersionId) {
        ModelVersion lastModelVersion = modelVersionService.getLastVersion(modelVersionId);
        return getRepository().findAllUserModelMemberByModelVersionIdAndUserId(SecurityUtils.getCurrentOrganIdOrElseThrow(),  lastModelVersion.getId(),userId);
    }



    @Override
    @Transactional(readOnly = true)
    public boolean isUserMemberOfModel(Long userId, Long modelVersionId) {
        return !getUserModelMember(userId, modelVersionId).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByModelVersionId(Long modelVersionId) {
        return getRepository().findAllModelMemberByModelVersionId(modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    /**
     * if model version not last version this method fetch last version
     * of model version and then fetch members
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getMembersOfModel(Long modelVersionId) {
        ModelVersion lastModelVersion = modelVersionService.getLastVersion(modelVersionId);
        return findByModelVersionId(lastModelVersion.getId());
    }

    public int getCountAllLastVersionByUserId(Long userId) {
        return getRepository().getCountAllLastVersionByUserId(userId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

}
