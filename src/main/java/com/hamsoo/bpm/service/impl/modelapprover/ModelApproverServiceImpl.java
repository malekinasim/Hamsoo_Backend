package com.hamsoo.bpm.service.impl.modelapprover;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.approverlog.ApproverLogDto;
import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.service.api.approverlog.ApproverLogService;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.identity.IoSupCustService;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelmemberrole.ModelMemberRoleService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory_;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.model.Model_;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover_;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember_;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole_;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelapprover.ModelApproverRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ModelApproverServiceImpl implements ModelApproverService {

    private ModelApproverRepository modelApproverRepository;
    private ApproverLogService approverLogService;
    private CodeCategoryService codeCategoryService;
    private UserService userService;
    private ModelVersionService modelVersionService;
    private ModelMemberService modelMemberService;
    private ModelMemberRoleService modelMemberRoleService;
    private ModelRoleService modelRoleService;
    private IdentityIoService identityIoService;
    private IoSupCustService ioSupCustService;
    private final String ADD = "add", DELETE = "delete";


    public ModelApproverServiceImpl(ModelApproverRepository modelApproverRepository,
                                    CodeCategoryService codeCategoryService,
                                    ApproverLogService approverLogService,
                                    UserService userService,
                                    ModelVersionService modelVersionService,
                                    ModelMemberService modelMemberService,
                                    ModelMemberRoleService modelMemberRoleService, ModelRoleService modelRoleService,
                                    IdentityIoService identityIoService,
                                    IoSupCustService ioSupCustService) {
        this.modelApproverRepository = modelApproverRepository;
        this.codeCategoryService = codeCategoryService;
        this.userService = userService;
        this.modelVersionService = modelVersionService;
        this.modelMemberService = modelMemberService;
        this.approverLogService = approverLogService;
        this.modelMemberRoleService = modelMemberRoleService;
        this.identityIoService = identityIoService;
        this.modelRoleService = modelRoleService;
        this.ioSupCustService = ioSupCustService;
    }


    @Override
    public ModelApproverRepository getRepository() {
        return modelApproverRepository;
    }

    @Override
    public ModelApprover findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelapprover.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.modelapprover.idIsNotValid"));
    }

    @Override
    public ModelApprover create(ModelApproverDto modelApproverDto) {
        Assert.notNull(modelApproverDto, "modelApproverDto is null");
        Optional<ModelApprover> find = getRepository()
                .findByModelVersionIdAndApproveGroupCodeAndCodeCategoryAndUserIdAndStatus(modelApproverDto.getModelVersionId(), modelApproverDto.getApproveGroupCode(), ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getUserId(), Status.ACTIVE.name());
        if (find.isPresent()) {
            Optional<User> optionalUser = userService.findById(find.get().getModelMemberRole().getModelMember().getMemberId());
            if (optionalUser.isPresent())
                throw new BusinessException("error.modelApprover.ukIsNotValid", optionalUser.get().getFullName());
            throw new BusinessException("error.modelApprover.error.modelApprover.ukIsNotValid1");
        }

        ModelApprover modelApprover = new ModelApprover();
        modelApprover.setLevel(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveLevelType.CATEGORY, modelApproverDto.getLevel()));
        modelApprover.setOrder(modelApproverDto.getOrder());
        modelApprover.setApproveGroup(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getApproveGroupCode()));
        modelApprover.setModelMemberRole(createModelRoleMemberApprover(modelApproverDto.getUserId(), modelApproverDto.getModelVersionId()));
        modelApprover.setDeleteable(modelApproverDto.getDeleteable());
        modelApprover.setDeleteable(modelApproverDto.getDeleteable());
        modelApprover.setRefrenceDescription(modelApproverDto.getRefrenceDescription());
        return save(modelApprover);
    }


    private Optional<ModelApprover> getModelApproverByOrder(Long modelVersionId, int oreder) {
        return getRepository().findAllByModelModelVersionIdAndOrder(modelVersionId, oreder);
    }


    private ModelMemberRole createModelRoleMemberApprover(Long userId, Long modelVersionId) {
        ModelRole modelRole = modelRoleService.findByCodeOrElseThrow(ModelRole.Code.MODEL_APPROVER.getCode());
        ModelMemberRoleDto modelMemberRoleDto = new ModelMemberRoleDto();
        //TODO change need  to create post/job/unit groups user  as approver
        modelMemberRoleDto.setMemberId(userId);
        modelMemberRoleDto.setMemberType(ModelMember.MemberType.USER.getCode());
        modelMemberRoleDto.setModelVersionId(modelVersionId);
        modelMemberRoleDto.setModelRoleId(modelRole.getId());
        return modelMemberRoleService.create(modelMemberRoleDto);
    }


    private List<ModelApprover> createAll(List<ModelApproverDto> modelApproverDtoList) {
        Assert.notNull(modelApproverDtoList, "modelApproverDto is null");
        List<ModelApprover> modelApprovers = new ArrayList<>();
        modelApproverDtoList.stream().forEach(modelApproverDto -> {
            Optional<ModelApprover> find = getRepository()
                    .findByModelVersionIdAndApproveGroupCodeAndCodeCategoryAndUserIdAndStatus(modelApproverDto.getModelVersionId(), modelApproverDto.getApproveGroupCode(), ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getUserId(), Status.ACTIVE.name());
            if (!find.isPresent()) {
                ModelApprover modelApprover = new ModelApprover();
                modelApprover.setLevel(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveLevelType.CATEGORY, modelApproverDto.getLevel()));
                modelApprover.setOrder(modelApproverDto.getOrder());
                modelApprover.setApproveGroup(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getApproveGroupCode()));
                modelApprover.setModelMemberRole(createModelRoleMemberApprover(modelApproverDto.getUserId(), modelApproverDto.getModelVersionId()));
                modelApprover.setDeleteable(modelApproverDto.getDeleteable());
                modelApprover.setRefrenceDescription(modelApproverDto.getRefrenceDescription());
                modelApprovers.add(modelApprover);
            }
        });

        return saveAll(modelApprovers);
    }

    @Override
    public ModelApproverDto mapToDto(ModelApprover modelApprover) {
        Assert.notNull(modelApprover, "modelApprover is null");
        modelApprover = findByIdOrElseThrow(modelApprover.getId());
        ModelApproverDto modelApproverDto = new ModelApproverDto();
        modelApproverDto.setId(modelApprover.getId());
        modelApproverDto.setLevel(modelApprover.getLevel().getCode());
        modelApproverDto.setLevelTitle(modelApprover.getLevel().getFaName());
        modelApproverDto.setOrder(modelApprover.getOrder());
        modelApproverDto.setApproveGroupCode(modelApprover.getApproveGroup().getCode());
        modelApproverDto.setApproveGroupCodeTitle(modelApprover.getApproveGroup().getFaName());
        modelApproverDto.setUserDto(userService.mapToDto(userService.findByIdOrElseThrow(modelApprover.getModelMemberRole().getModelMember().getMemberId())));
        modelApproverDto.setUserId(modelApprover.getModelMemberRole().getModelMember().getMemberId());
        //TODO just add user as approver must change
        modelApproverDto.setModelVersionDto(modelVersionService.mapToDto(modelApprover.getModelMemberRole().getModelMember().getModelVersion()));
        modelApproverDto.setModelVersionId(modelApprover.getModelMemberRole().getModelMember().getModelVersion().getId());

        modelApproverDto.setDeleteable(modelApprover.getDeleteable());
        modelApproverDto.setResponseDescription(modelApprover.getResponseDescription());
        modelApproverDto.setRefrenceDescription(modelApprover.getRefrenceDescription());

        modelApproverDto.setLastApproverLog(approverLogService.findLastLogForModelApprover(modelApprover.getId()));

        return modelApproverDto;
    }

    @Override
    public List<ModelApproverDto> list(Long modelVersionId, String approverGroup) {
        Optional<Specification<ModelApprover>> optionalSpecification = createSpecification(modelVersionId, approverGroup);
        List<ModelApprover> approvers = findAll(optionalSpecification.get());
        List<ModelApproverDto> content = approvers.stream().map(this::mapToDto).sorted(
                Comparator.comparingInt(ModelApproverDto::getOrder)
        ).toList();
        return content;
    }

    @Override
    public ModelApproverDto edit(ModelApproverDto modelApproverDto) {
        Assert.notNull(modelApproverDto, "ModelApproverDto is null");
        Assert.notNull(modelApproverDto.getId(), "ModelApproverDto is null");
        ModelApprover modelApprover = findByIdOrElseThrow(modelApproverDto.getId());
        modelApprover.setLevel(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveLevelType.CATEGORY, modelApproverDto.getLevel()));
        modelApprover.setOrder(modelApproverDto.getOrder());
        modelApprover.setApproveGroup(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getApproveGroupCode()));
        modelApprover.setDeleteable(modelApproverDto.getDeleteable());
        modelApprover.setRefrenceDescription(modelApproverDto.getRefrenceDescription());
        modelApprover.setResponseDescription(modelApproverDto.getResponseDescription());
        modelApprover = save(modelApprover);
        return mapToDto(modelApprover);
    }

    private void editAll(List<ModelApproverDto> modelApproverDtoList) {

        Assert.notNull(modelApproverDtoList, "modelApproverDtoList is null");
        List<ModelApprover> modelApproverList = modelApproverDtoList.stream().map(modelApproverDto -> {
            ModelApprover modelApprover = findByIdOrElseThrow(modelApproverDto.getId());
            modelApprover.setLevel(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveLevelType.CATEGORY, modelApproverDto.getLevel()));
            modelApprover.setOrder(modelApproverDto.getOrder());
            modelApprover.setApproveGroup(codeCategoryService.findByCategoryAndCode(ModelApprover.ApproveGroupType.CATEGORY, modelApproverDto.getApproveGroupCode()));
            modelApprover.setDeleteable(modelApproverDto.getDeleteable());
            modelApprover.setRefrenceDescription(modelApproverDto.getRefrenceDescription());
            modelApprover.setResponseDescription(modelApproverDto.getResponseDescription());
            return modelApprover;
        }).toList();

        saveAll(modelApproverList);
    }

    @Override
    public void passive(Long id) {
        ModelApprover modelApprover = findByIdOrElseThrow(id);
        if (!modelApprover.getDeleteable()) {
            Optional<User> optionalUser = userService.findById(modelApprover.getModelMemberRole().getModelMember().getMemberId());
            if (optionalUser.isPresent())
                throw new BusinessException("error.modelApprover.notDeletable", optionalUser.get().getFullName());
            throw new BusinessException("error.modelApprover.notDeletable1");
        }
        modelApprover.setStatus(Status.PASSIVE.name());
        modelApprover.getModelMemberRole().setStatus(Status.PASSIVE.name());
        List<ModelApprover> greaterOrderModelApprover = getRepository().findAllByModelVersionIdAndOrderGreaterThan(id, modelApprover.getOrder());
        greaterOrderModelApprover.stream().forEach(modelApprover1 -> modelApprover1.setOrder(modelApprover1.getOrder() - 1));
        greaterOrderModelApprover.add(modelApprover);
        saveAll(greaterOrderModelApprover);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passiveAll(List<Long> list) {
        Assert.notNull(list, "list is null");
        List<ModelApprover> approvers = list.stream().map(id -> {
            ModelApprover modelApprover = findByIdOrElseThrow(id);
            modelApprover.setStatus(Status.PASSIVE.name());
            modelApprover.getModelMemberRole().setStatus(Status.PASSIVE.name());
            if (modelApprover.getApproverLogs() != null) {
                modelApprover.getApproverLogs().forEach(
                        log -> log.setStatus(Status.PASSIVE.name())
                );
            }
            return modelApprover;
        }).toList();
        saveAll(approvers);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<ModelApproverDto> generateDefaultModelApproverList(Long modelVersionId) {
        Assert.notNull(modelVersionId, "modelVersionId is null");
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);


        if (modelVersion.getStatus().getCode().equals(ModelVersion.Status.DRAFT.getCode())) {
            List<ModelApproverDto> curAllApprover = this.list(modelVersionId, null);
            int maxOrder = 0;
            if (!CollectionUtils.isEmpty(curAllApprover))
                maxOrder = curAllApprover.stream().mapToInt(modeApprover -> modeApprover.getOrder()).max().orElse(0);

            //getaddandDeleteMembrOfOwnerGroup
            List<ModelMember> listModelOwners = modelMemberService.findByModelVersionIdAndModelRoleCode(modelVersionId, ModelRole.Code.MODEL_OWNER.getCode()).stream().distinct().toList();
            HashMap<String, List<ModelApproverDto>> mapGroup = getDeleteAndAdd(maxOrder, listModelOwners,
                    modelVersionId, ModelApprover.ApproveGroupType.OWNERSGROUP, curAllApprover);

            //getaddandDeleteMembrOfSupplierGroup
            maxOrder = maxOrder + mapGroup.get(ADD).size();
            List<ModelMember> listSuplierModelOwners = new ArrayList<>();

            identityIoService.findInternalActiveByModelVersionId(IdentityIo.Type.INPUT.getCode(), modelVersionId).stream().forEach(io -> {
                ioSupCustService.findAllIoCatalogActiveInternalIoSupCust(modelVersionId, io.getId())
                        .forEach(ioSupCust ->
                                listSuplierModelOwners.addAll(modelMemberService.findByModelVersionIdAndModelRoleCode(ioSupCust.getInternalSupCust().getId(), ModelRole.Code.MODEL_OWNER.getCode())));
            });
            HashMap<String, List<ModelApproverDto>> mapGroupOfSupplier = getDeleteAndAdd(maxOrder, listSuplierModelOwners, modelVersionId, ModelApprover.ApproveGroupType.SUPPLIERSGROUP, curAllApprover);


            //getaddandDeleteMembrOfCustomerGroup
            maxOrder = maxOrder + mapGroupOfSupplier.get(ADD).size();

            List<ModelMember> listCustomerModelOwners = new ArrayList<>();

            identityIoService.findInternalActiveByModelVersionId(IdentityIo.Type.OUTPUT.getCode(), modelVersionId).stream().forEach(io -> {
                ioSupCustService.findAllIoCatalogActiveInternalIoSupCust(modelVersionId, io.getId())
                        .forEach(ioSupCust ->
                                listCustomerModelOwners.addAll(modelMemberService.findByModelVersionIdAndModelRoleCode(ioSupCust.getInternalSupCust().getId(), ModelRole.Code.MODEL_OWNER.getCode())));
            });
            HashMap<String, List<ModelApproverDto>> mapGroupOfCustomer = getDeleteAndAdd(maxOrder, listCustomerModelOwners, modelVersionId, ModelApprover.ApproveGroupType.CUSTOMERSGROUP, curAllApprover);

            //add
            List<ModelApproverDto> addedApprover = new ArrayList<>();
            addedApprover.addAll(mapGroup.get(ADD));
            addedApprover.addAll(mapGroupOfSupplier.get(ADD));
            addedApprover.addAll(mapGroupOfCustomer.get(ADD));
            this.createAll(addedApprover);

            //delete
            List<ModelApproverDto> deletedApprover = new ArrayList<>();
            deletedApprover.addAll(mapGroup.get(DELETE));
            deletedApprover.addAll(mapGroupOfSupplier.get(DELETE));
            deletedApprover.addAll(mapGroupOfCustomer.get(DELETE));
            this.passiveAll(deletedApprover.stream().map(ModelApproverDto::getId).toList());

        }
        return this.list(modelVersionId, null);

    }

    @Override
    public List<ModelApproverDto> ChangeModelApproverOrder(Long modelVersionId,
                                                           Long srcModelApproverId,
                                                           Long targetModelApproverId) {

        ModelApprover srcModelApprover = this.findByIdOrElseThrow(srcModelApproverId);
        ModelApprover targetModelApprover = this.findByIdOrElseThrow(targetModelApproverId);

        ModelApproverDto srcModelApproverDto = this.mapToDto(srcModelApprover);
        ModelApproverDto targetModelApproverDto = this.mapToDto(targetModelApprover);


        Optional<ApproverLogDto> srcLastApproverLog = approverLogService.findLastLogForModelApprover(srcModelApproverId);
        Optional<ApproverLogDto> targetLastApproverLog = approverLogService.findLastLogForModelApprover(targetModelApproverId);

        if (!srcLastApproverLog.isPresent() ||
                !targetLastApproverLog.isPresent()) {
            int srcCurOrder = srcModelApprover.getOrder();
            int targetCurOrder = targetModelApprover.getOrder();
            List<ModelApproverDto> list = new ArrayList<>();
            list.add(srcModelApproverDto);
            list.add(targetModelApproverDto);

            if (srcCurOrder > targetCurOrder) {
                srcModelApproverDto.setOrder(targetCurOrder);
                targetModelApproverDto.setOrder(targetCurOrder + 1);
                List<ModelApprover> middleOrderApprover = getRepository()
                        .findAllByModelVersionIdAndOrderBetween(modelVersionId, targetCurOrder + 1, srcCurOrder - 1);
                list.addAll(middleOrderApprover.stream().map(item -> mapToDto(item))
                        .peek(modelApprover -> modelApprover.setOrder(modelApprover.getOrder() + 1)).toList());

            } else if (srcCurOrder < targetCurOrder) {
                srcModelApproverDto.setOrder(targetCurOrder);
                targetModelApproverDto.setOrder(targetCurOrder - 1);
                List<ModelApprover> middleOrderApprover = getRepository()
                        .findAllByModelVersionIdAndOrderBetween(modelVersionId, srcCurOrder + 1, targetCurOrder - 1);
                list.addAll(middleOrderApprover.stream().map(item -> mapToDto(item))
                        .peek(modelApprover -> modelApprover.setOrder(modelApprover.getOrder() - 1)).toList());

            }
            this.editAll(list);
            return this.list(modelVersionId, null);
        } else throw new BusinessException("error.modelApprover.canNotChangeOrder");
    }

    @Override
    public Optional<ModelApprover> getNextStepModelApprover(Long modelVersionId) {
        return this.getRepository().findMinOrderByModelIdAndApproverLogsEmpty(modelVersionId);
    }

    @Override
    public void passiveAllNotCheckModelApprover(Long modelVersionId, Long modelApproveId) {
        List<String> statusList = new ArrayList<>();
        statusList.add(ApproverLog.ApproveLogState.APPROVE.getCode());
        statusList.add(ApproverLog.ApproveLogState.ACKNOWLEDGE.getCode());
        List<ModelApprover> modelApprovers = this.getRepository().findAllNotCheckModelApprover(modelVersionId, statusList);
        this.passiveAll(modelApprovers.stream().map(ModelApprover::getId).filter(id -> !id.equals(modelApproveId)).toList());
    }

    @Override
    public SearchDto getModelApproveListForUser(ModelApproverDto modelApproverDto, int page, int size) {
        Pageable pageable = PageUtils.getInstance(page, size, null, null);

        Page<ModelDto> modelPage = modelApproverRepository.findAllModelApproveListForUser(
                pageable,
                SecurityUtils.getCurrentOrganIdOrElseThrow(),
                SecurityUtils.getCurrentUserOrElseThrow().getId(),
                ModelVersion.Status.FOR_APPROVE.getCode(),
                ModelRole.Code.MODEL_APPROVER.getCode(),
                Arrays.asList(ApproverLog.ApproveLogState.FORYOURINFO.getCode(),
                        ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode()),
                modelApproverDto.getModelName(),
                modelApproverDto.getModelTypeCode(),
                modelApproverDto.getApproveLevel()
        );


        List<ModelDto> modelDtos = modelPage.getContent();

        return new SearchDto.Builder().content(modelDtos)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private HashMap<String, List<ModelApproverDto>> getDeleteAndAdd(final int finalMaxOrder,
                                                                    List<ModelMember> listModelMember,
                                                                    Long modelVersionId,
                                                                    ModelApprover.ApproveGroupType approveGroupType,
                                                                    List<ModelApproverDto> curAllApprover) {

        List<ModelMember> distinctModelMembers = ListIterate.distinct(listModelMember, HashingStrategies.fromFunction(member ->
                member.getMemberType().getCode() + " " + member.getMemberId()
        ));
        AtomicInteger order = new AtomicInteger(finalMaxOrder);
        HashMap<String, List<ModelApproverDto>> result = new HashMap<>();

        // List<ModelApprover> curGroupApprover = this.getApproverGroupMember(modelVersionId,approveGroupType);
        List<ModelApproverDto> curGroupApprover = curAllApprover.stream().filter(
                modelApprover -> modelApprover.getApproveGroupCode().equals(approveGroupType.getCode())
        ).toList();

        List<ModelApproverDto> newModelApprover = distinctModelMembers.stream().filter(mem ->
                !curGroupApprover.stream().anyMatch(approver -> approver.getUserDto().getId().equals(mem.getMemberId()))).map(
                member -> {
                    ModelApproverDto modelApproverDto = new ModelApproverDto();

                    order.set(order.get() + 1);
                    modelApproverDto.setOrder(order.get());

                    modelApproverDto.setLevel(ModelApprover.ApproveLevelType.APPROVE.getCode());
                    modelApproverDto.setApproveGroupCode(approveGroupType.getCode());
                    modelApproverDto.setModelVersionId(modelVersionId);
                    modelApproverDto.setDeleteable(false);
                    modelApproverDto.setUserId(member.getMemberId());
                    return modelApproverDto;
                }
        ).toList();
        result.put(ADD, newModelApprover);

        List<ModelApproverDto> deletedModelApprover = curGroupApprover.stream().filter(approver ->
                !distinctModelMembers.stream().anyMatch(mem -> mem.getMemberId().equals(approver.getUserDto().getId()))
        ).toList();
        result.put(DELETE, deletedModelApprover);
        return result;
    }

    private Optional<Specification<ModelApprover>> createSpecification(Long modelVersionId, String approverGroup) {
        List<Specification<ModelApprover>> specificationList = new ArrayList<>();
        Specification<ModelApprover> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ModelApprover_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        Specification<ModelApprover> organAndApproveGroupEq = (root, query, criteriaBuilder) -> {
            Join<ModelApprover, ModelMemberRole> modelMemberRoleJoin = root.join(ModelApprover_.MODEL_MEMBER_ROLE);
            Join<ModelMemberRole, ModelMember> modelMemberJoin = modelMemberRoleJoin.join(ModelMemberRole_.MODEL_MEMBER);
            Join<ModelMember, ModelVersion> modelVersionJoin = modelMemberJoin.join(ModelMember_.MODEL_VERSION);
            Join<ModelVersion, Model> modelJoin = modelVersionJoin.join(ModelVersion_.MODEL);
            Join<Model, Organ> organJoin = modelJoin.join(Model_.ORGAN);
            Join<ModelApprover, CodeCategory> codeCategoryJoin = root.join(ModelApprover_.approveGroup);

            Predicate[] predicates = new Predicate[3];
            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[1] = criteriaBuilder.equal(modelVersionJoin.get(ModelVersion_.ID), modelVersionId);
            predicates[2] = criteriaBuilder.equal(modelJoin.get(Model_.STATUS), Status.ACTIVE.name());

            if (StringUtils.hasText(approverGroup)) {
                ArrayUtils.add(predicates, criteriaBuilder.equal(codeCategoryJoin.get(CodeCategory_.CODE), approverGroup));
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organAndApproveGroupEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }
}

