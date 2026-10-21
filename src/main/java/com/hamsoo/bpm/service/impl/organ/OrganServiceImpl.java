package com.hamsoo.bpm.service.impl.organ;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.oauthprovider.OauthProviderDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.organ.OrganRepository;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.saleplan.SalePlanService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganServiceImpl implements OrganService {
    private final OrganRepository organRepository;
    private final UserService userService;
    private final FilesService filesService;
    private final RoleMemberService roleMemberService;
    private final UserOrganService userOrganService;
    private final OrganPlanService organPlanService;
    private final SalePlanService salePlanService;
    private final ModelService modelService;
    private final ModelDocumentService modelDocumentService;



    public OrganServiceImpl(OrganRepository organRepository, UserService userService, FilesService filesService,
                            @Lazy RoleMemberService roleMemberService,
                            @Lazy UserOrganService userOrganService,
                            @Lazy OrganPlanService organPlanService,
                            SalePlanService salePlanService,
                            @Lazy ModelService modelService,
                            ModelDocumentService modelDocumentService) {
        this.organRepository = organRepository;
        this.userService = userService;
        this.filesService = filesService;
        this.roleMemberService = roleMemberService;
        this.userOrganService = userOrganService;
        this.organPlanService = organPlanService;
        this.salePlanService = salePlanService;
        this.modelService = modelService;
        this.modelDocumentService = modelDocumentService;
    }

    @Override
    public OrganRepository getRepository() {
        return organRepository;
    }

    @Override
    public Organ findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.organ.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.organ.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Organ getUserPrivateOrganObj(Account account) {
        Organ organ = new Organ();
        organ.setName(account.getUsername() + "_WorkSpace");
        organ.setDescription(ResourceBundleUtils.getMessage("MyWorkSpace"));
        organ.setCreatedBy(account.getUser());
        organ.setStatus(Status.ACTIVE.name());
        organ.setModifiedBy(account.getUser());
        organ.setOrganType(Organ.OrganType.PRIVATE);
        Optional<SalePlan> freeSalePlan = salePlanService.findBySaleServiceTypeAndStatus(SalePlan.ServicesType.FREE.name(), Status.ACTIVE.name());

        Organ org= save(organ);


        if (freeSalePlan.isPresent()) {
            OrganPlanDto organPlanDto = new OrganPlanDto();
            organPlanDto.setOrganId(org.getId());
            organPlanDto.setMaxCapacity(freeSalePlan.get().getMaxCapacity());
            organPlanDto.setMaxModelCount(freeSalePlan.get().getMaxModelCount());
            organPlanDto.setMaxUserCount(freeSalePlan.get().getMaxUserCount());

//            List<SalePlanFeatureDto> salePlanFeatureDtos = salePlanFeatureservice.findAllFeatureOfSalePlanByFeature(freeSalePlan.get().getId(),
//                    Arrays.asList(Feature.CalcType.MODEL_COUNT.getCode(), Feature.CalcType.USER_COUNT.getCode(), Feature.CalcType.UPLOAD_VOLUME.getCode()));
//            if (salePlanFeatureDtos.stream().anyMatch(fe -> fe.getFeatureDto().getCalcType().equals(Feature.CalcType.UPLOAD_VOLUME.getCode()))) {
//                organPlanDto.setMaxCapacity(
//                        salePlanFeatureDtos.stream().filter(fe -> fe.getFeatureDto().getCalcType()
//                                        .equals(Feature.CalcType.UPLOAD_VOLUME.getCode()))
//                                .findFirst().get().getCount().intValue());
//            }
//
//            if (salePlanFeatureDtos.stream().anyMatch(fe -> fe.getFeatureDto().getCalcType().equals(Feature.CalcType.MODEL_COUNT.getCode()))) {
//                organPlanDto.setMaxModelCount(
//                        salePlanFeatureDtos.stream().filter(fe -> fe.getFeatureDto().getCalcType()
//                                        .equals(Feature.CalcType.MODEL_COUNT.getCode()))
//                                .findFirst().get().getCount().intValue());
//
//            }
//            if (salePlanFeatureDtos.stream().anyMatch(fe -> fe.getFeatureDto().getCalcType().equals(Feature.CalcType.USER_COUNT.getCode()))) {
//                organPlanDto.setMaxModelCount(
//                        salePlanFeatureDtos.stream().filter(fe -> fe.getFeatureDto().getCalcType()
//                                        .equals(Feature.CalcType.USER_COUNT.getCode()))
//                                .findFirst().get().getCount().intValue());
//            }
//
            organPlanDto.setAmountPaid(BigInteger.ZERO);
            organPlanService.create(organPlanDto);
        }
        return org;
    }

    @Override
    public void deleteOrgan(Long id) {
        Organ organ = findByIdOrElseThrow(id);
        delete(organ);
    }

    @Override
    public Organ findByNameOrElseThrow(String name) {
        return organRepository.findByName(name).orElseThrow(() -> new BusinessException("error.organ.NameNotValid"));
    }


    @Override
    public Optional<Organ> getDefualtUserOrgan(String username) {
        User user = userService.findByUsernameOrElseThrow(username);
        return organRepository.findUserDefaultOrgan(user.getId(), true);
    }


    @Override
    public OrganDto mapToDto(Organ organ) {

        organ = this.findByIdOrElseThrow(organ.getId());
        OrganDto organDto = new OrganDto();

        organDto.setId(organ.getId());
        organDto.setName(organ.getName());
        organDto.setDescription(organ.getDescription());
        Optional<User> organOwner =
                organ.getOrganType().equals(Organ.OrganType.PRIVATE) ?
                        userService.findOrganOwner(organ.getId()) : Optional.empty();
        String ownerName = organOwner.map(User::getFullName).orElse(null);
        Long ownerId = organOwner.map(User::getId).orElse(null);
        if(organOwner.isPresent()) {
            if (SecurityUtils.getCurrentOrgan().isPresent() &&
                    SecurityUtils.getCurrentUser().isPresent() &&
                    !organOwner.get().getId().equals(SecurityUtils.getCurrentUser().get().getId()) &&
                    organ.getOrganType().equals(Organ.OrganType.PRIVATE)
                    && StringUtils.hasText(ownerName) &&
                    StringUtils.hasText(organ.getDescription())
            ) {
                organDto.setDescription(organ.getDescription().substring(0, organ.getDescription().length() - 2).concat(ownerName));
            } else {
                organDto.setDescription(organ.getDescription());
            }
        }else{
            organDto.setDescription(organ.getDescription());
        }
        if (organ.getOauthProvider() != null) {
            organDto.setOauthProviderDto(new OauthProviderDto(organ.getOauthProvider()));
        }

        organDto.setStatus(organ.getStatus());
        organDto.setOrganType(organ.getOrganType());
        organDto.setOwnerName(ownerName);
        organDto.setOwnerUserId(ownerId);

        if (organ.getLogo() != null && organ.getOrganType().equals(Organ.OrganType.ORGAN)) {
            organDto.setLogoId(organ.getLogo().getId());
        } else if (organ.getOrganType().equals(Organ.OrganType.PRIVATE)) {

            List<RoleMember> roleMembers = roleMemberService.findAllByOrganIdAndRoleName(organ.getId(), Role.OrganRole.ADMIN_ROLE.getCode());

            Optional<User> adminUser = roleMembers.stream().map(RoleMember::getUser).findFirst();

            if (adminUser.isPresent() && adminUser.get().getImage() != null) {
                organDto.setLogoId(adminUser.get().getImage().getId());
            }
        }

        if (SecurityUtils.getCurrentUser().isPresent())
            organDto.setDefault(organOwner.isPresent() && organOwner.get().getId().equals(SecurityUtils.getCurrentUserOrElseThrow().getId()));
        else
            organDto.setDefault(false);
        if(SecurityUtils.getCurrentOrgan().isPresent()) {
            Optional<OrganPlan> organPlan = organPlanService.getLastOrganSalePlan(SecurityUtils.getCurrentOrgan().get().getId());
            if (organPlan.isPresent()) {
                organDto.setLastOrganPlan(organPlanService.mapToDto(organPlan.get()));
                organDto.setSalePlanId(organPlan.get().getSalePlan().getId());
                organDto.setMaxCapacity(organPlan.get().getMaxCapacity());
                organDto.setMaxUserCount(organPlan.get().getMaxUserCount());
                organDto.setMaxModelCount(organPlan.get().getMaxModelCount());
                organDto.setAmountPaid(organPlan.get().getAmountPaid());
            }
        }
        return organDto;
    }


    @Override
    public OrganDto mapToDtoByOrganMemberRole(Organ organ) {
        OrganDto organDto = this.mapToDto(organ);
        organDto.setRoleMemberDtos(roleMemberService.findAllByOrganId(organ.getId()).stream()
                .map(roleMember -> roleMemberService.mapToDto(roleMember)).toList());
        return organDto;
    }

    @Override
    public Optional<Organ> findByIdAndUserName(@NotNull String curOrgan, String username) {
        User user = userService.findByUsernameOrElseThrow(username);
        return this.getRepository().findByIdAndUserId(user.getId(), Long.parseLong(curOrgan));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Organ create(OrganDto organDto, MultipartFile image) {
        Organ organ = new Organ();
        organ.setName(organDto.getName());
        organ.setDescription(organDto.getName());
        organ.setStatus(Status.ACTIVE.name());
        organ.setOrganType(Organ.OrganType.ORGAN);

        Organ finalOrgan = save(organ);
        if (image != null && !image.isEmpty())
            updateOrganImage(image, organ.getId());

        if (!CollectionUtils.isEmpty(organDto.getRoleMemberDtos())) {
            List<UserOrganDto> userOrganDtos = organDto.getRoleMemberDtos().stream().map(roleMemberDto -> {
                UserOrganDto userOrganDto = new UserOrganDto();
                userOrganDto.setOrganId(finalOrgan.getId());
                userOrganDto.setUserId(roleMemberDto.getUserId());
                return userOrganDto;
            }).toList();
            userOrganService.createAll(userOrganDtos);
            organDto.getRoleMemberDtos().stream().forEach(roleMemberDto -> {
                roleMemberDto.setOrganId(organ.getId());
            });
            roleMemberService.createAll(organDto.getRoleMemberDtos());
        }
        if (organDto.getSalePlanId() != null) {
            OrganPlanDto organPlanDto = new OrganPlanDto();
            organPlanDto.setOrganId(finalOrgan.getId());
            organPlanDto.setSalePlanId(organDto.getSalePlanId());
            organPlanDto.setMaxCapacity(organDto.getMaxCapacity());
            organPlanDto.setMaxUserCount(organDto.getMaxUserCount());
            organPlanDto.setMaxModelCount(organDto.getMaxModelCount());
            organPlanDto.setAmountPaid(organDto.getAmountPaid());
            organPlanService.create(organPlanDto);
        }
        return finalOrgan;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Organ edit(OrganDto organDto, MultipartFile image) {
        Organ organ = findByIdOrElseThrow(organDto.getId());
        organ.setName(organDto.getName());
        organ.setDescription(organDto.getName());
        this.save(organ);
        if (image != null && !image.isEmpty())
            updateOrganImage(image, organ.getId());

        List<UserOrganDto> userOrganDtoList = userOrganService.findAllUserOfOrgan(organ.getId());
        List<Long> userDtos = userOrganDtoList.stream().map(UserOrganDto::getUserId).distinct().toList();
        organDto.getRoleMemberDtos().forEach(roleMemberDto -> roleMemberDto.setOrganId(organ.getId()));
        userOrganService.createAll(organDto.getRoleMemberDtos().stream().filter(roleMemberDto ->
                !userDtos.contains(roleMemberDto.getUserId())).map(roleMemberDto -> {
            UserOrganDto userOrganDto = new UserOrganDto();
            userOrganDto.setOrganId(organ.getId());
            userOrganDto.setUserId(roleMemberDto.getUserId());
            return userOrganDto;
        }).toList());


        userOrganService.passiveAllOrganMemberAndModelMemberOfOrgan(userOrganDtoList.stream().filter(userOrganDto ->
                !organDto.getRoleMemberDtos().stream().map(RoleMemberDto::getUserId).toList().contains(userOrganDto.getUserId())
        ).toList());

        List<RoleMember> roleMembers = roleMemberService.findAllByOrganId(organDto.getId());

        roleMemberService.createAll(organDto.getRoleMemberDtos().stream().filter(roleMemberDto ->
                !roleMembers.stream().map(RoleMember::getId).toList().contains(roleMemberDto.getId())
        ).toList());

        roleMemberService.passiveAll(roleMembers.stream().filter(roleMember ->
                !organDto.getRoleMemberDtos().stream().map(RoleMemberDto::getId).toList().contains(roleMember.getId())
        ).toList());

        return organ;
    }

    @Transactional(rollbackFor = Throwable.class)
    Long updateOrganImage(MultipartFile file, Long id) {
        FilesDto filesDto = filesService.map(file, Files.FileType.BYTE);
        Organ organ = findByIdOrElseThrow(id);
        if (organ.getLogo() == null) {
            Files image = filesService.create(filesDto);
            filesDto.setId(image.getId());
            organ.setLogo(image);
            save(organ);
        } else {
            filesDto.setId(organ.getLogo().getId());
            filesService.edit(filesDto);
        }
        return filesDto.getId();
    }

    @Override
    public void passive(Long id) {
        Organ organ = findByIdOrElseThrow(id);
        if (organ.getOrganType().equals(Organ.OrganType.PRIVATE))
            throw new BusinessException("error.organ.passivePrivateOrgan");
        organ.setStatus(Status.PASSIVE.name());
        this.save(organ);
    }

    @Override
    public SearchDto list(OrganDto organDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Organ>> optionalSpecification = createSpecification(organDto);
        Page<Organ> organPage = findAll(optionalSpecification.get(), pageable);
        List<OrganDto> content = organPage.map(this::mapToDtoByOrganMemberRole).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(organPage.getTotalElements())
                .totalPages(organPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public List<OrganDto> getUserOrgans(Long userId) {
        return organRepository.findUserOrgans(userId).stream().map(this::mapToDto).toList();
    }

    @Override
    public Integer getOrganUsersCount(Long organId) {
        return userOrganService.countUserOrganByOrganId(organId);
    }
    @Override
    public Integer getOrganModelCount(Long id) {
        return modelService.countModelByOrganId(id);
    }

    @Override
    public BigInteger getOrganModelCapacity(Long id) {
       // BigInteger modelBpmCapacity = this.getRepository().getOrganModelBpmCapacity(id);

        BigInteger modelDocumentCapacity =  modelDocumentService.getOrganModelDocumentCapacity(id);
      //  return modelBpmCapacity.add(modelDocumentCapacityCapacity) ;
        return  modelDocumentCapacity;

    }

    private Optional<Specification<Organ>> createSpecification(OrganDto organDto) {
        List<Specification<Organ>> specificationList = new ArrayList<>();
        Specification<Organ> organActive = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[1];

            predicates[0] = criteriaBuilder.equal(root.get(Organ_.STATUS), Status.ACTIVE.name());

            if (organDto.getName() != null) {
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.like(criteriaBuilder.lower(root.get(Organ_.NAME)),
                        "%" + organDto.getName().toLowerCase() + "%"));
            }
            if (organDto.getOrganType() != null) {
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.equal(root.get(Organ_.ORGAN_TYPE),
                        organDto.getOrganType()));
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organActive);
        return SpecificationUtils.andSpecifications(specificationList);

    }

}
