package com.hamsoo.bpm.service.impl.user;


import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.common.utils.wrapper.MailService;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.signup.RegisterRequest;
import com.hamsoo.bpm.model.dto.signup.SignUpRequest;
import com.hamsoo.bpm.model.dto.signup.VerifyUserMailDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.user.User_;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.user.UserRepository;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final OrganService organService;
    private final AccountService accountService;
    private final FilesService filesService;
    private final RoleService roleService;
    private final RoleMemberService roleMemberService;


    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${hamsoo.mail.templates.path}")
    private String templatePath;

    @Value("${hamsoo.sinup.mail.template.name}")
    private String templateName;


    @Value("${hamsoo.confirmEmail.url}")
    private String confirmEmailUrl;

    @Value("${hamsoo.logo.url}")
    private String hamsooLogoUrl;

    @Value("${hamsoo.mail.verify.url}")
    private String mailVerifyUrl;


    public UserServiceImpl(UserRepository userRepository, MailService mailService, @Lazy OrganService organService,
                           @Lazy AccountService accountService, FilesService filesService,
                           RoleService roleService, RoleMemberService roleMemberService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.organService = organService;
        this.accountService = accountService;
        this.filesService = filesService;
        this.roleService = roleService;
        this.roleMemberService = roleMemberService;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public User findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.user.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.user.idIsNotValid"));
    }


    @Override
    public User findByUsernameOrElseThrow(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("error.user.usernameIsNotValid");
        }
        return getRepository().findByUsername(username).orElseThrow(() -> new BusinessException("error.user.usernameIsNotValid"));
    }

    @Override
    public User findByUserIdAndCurrentOrganOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.user.idIsNotValid");
        }
        return getRepository().findByUserIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.user.idIsNotValid"));
    }

    @Override
    public User getUserObj(SignUpRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setFamily(signUpRequest.getFamily());
        user.setEmail(signUpRequest.getMail());
        user.setMobile(signUpRequest.getMobile());
        user.setNationalCode(signUpRequest.getNationalCode());
//        user.setCreatedBy(signUpRequest.getUsername());
//        user.setModifiedBy(signUpRequest.getUsername());
        user.setCompleted(true);
        user.setMailVerify(false);
//        User u= save(user);
        user.setModifiedBy(user);
        user.setCreatedBy(user);
        return save(user);
    }

    @Override
    public User getUserObj(RegisterRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setFamily(signUpRequest.getFamily());
        user.setEmail(signUpRequest.getMail());
        user.setMobile(signUpRequest.getMobile());
        user.setNationalCode(signUpRequest.getNationalCode());
        user.setCompleted(true);
        user.setMailVerify(false);
        return save(user);
    }

    @Override
    public User findByUserIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.user.idIsNotValid");
        }
        return getRepository().findById(id)
                .orElseThrow(() -> new BusinessException("error.user.idIsNotValid"));
    }

    @Override
    public void deleteUser(Long id) {
        User user = findByIdOrElseThrow(id);
        delete(user);
    }

    @Override
    public void sendVerificationEmail(User user, String code) throws UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String senderName = ResourceBundleUtils.getMessage("hamssoo.team");
        String subject = ResourceBundleUtils.getMessage("hamsoo.mail.verfiy");
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName() + " " + user.getFamily());
        params.put("confirmEmail", confirmEmailUrl);
        params.put("hamsooLogo", hamsooLogoUrl);
        params.put("url", URI.create(mailVerifyUrl + "?code=" + code + "&mail=" + toAddress));

        Map<String, ClassPathResource> inlinResources = new HashMap<>();
        ClassPathResource email = new ClassPathResource(confirmEmailUrl);
        if (email.exists())
            inlinResources.put("confirmEmail", email);

        ClassPathResource hamsoo = new ClassPathResource(hamsooLogoUrl);
        if (hamsoo.exists())
            inlinResources.put("hamsooLogo", hamsoo);

        mailService.send(fromAddress, toAddress, subject, senderName, templatePath, templateName, params, inlinResources);
    }

    @Override
    public Optional<User> findByNationalCode(String nationalCode) {
        return getRepository().findByNationalCode(nationalCode);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto mapToDto(User user) {
        Assert.notNull(user, "user is null");
        user = findByIdOrElseThrow(user.getId());
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setFamily(user.getFamily());
        userDto.setNationalCode(user.getNationalCode());
        userDto.setEmail(user.getEmail());
        userDto.setMobile(user.getMobile());
        userDto.setComplated(user.getCompleted());
        userDto.setFullName(user.getName() + " " + user.getFamily());
        userDto.setImageId(user.getImage() != null ? user.getImage().getId() : null);
        if (SecurityUtils.getCurrentOrgan().isPresent()) {
            OrganDto organDto = organService.mapToDto(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
            userDto.setCurOrgan(organDto);
        }
        if (SecurityUtils.getCurrentAccount().isPresent()) {
            userDto.setUsername(SecurityUtils.getCurrentAccount().get().getUsername());
        }
        return userDto;

    }

    @Override
    @Transactional(readOnly = true)
    public UserDto mapToDtoByRoleInfoAndOrgans(User user, Account account) {
        UserDto userDto = mapToDto(user);
        userDto.setUsername(account.getUsername());
        userDto.setOrgans(organService.getUserOrgans(user.getId()));
        List<RoleDto> roleDtos = new ArrayList<>();
        //find all user role in organ level
        if (SecurityUtils.getCurrentOrgan().isPresent()) {
            List<RoleMember> roleMembers = roleMemberService.findAllByUserAndOrgan(userDto.getId(), SecurityUtils.getCurrentOrgan().get().getId());
            roleDtos.addAll(roleMembers.stream()
                    .map(roleMem -> roleService.mapToDtoByResourceInfo(roleMem.getRole(), SecurityUtils.getCurrentOrgan().get().getId())).collect(Collectors.toList()));
        }
        //find all user role in application level
        List<RoleMember> appRoleMembers = roleMemberService.findAllAppBasedRoleByUser(userDto.getId());
        roleDtos.addAll(appRoleMembers.stream()
                .map(roleMem -> roleService.mapToDtoByResourceInfo(roleMem.getRole(),SecurityUtils.getCurrentOrgan().isPresent() ? SecurityUtils.getCurrentOrgan().get().getId() :
                              null
                        )).collect(Collectors.toList()));
        userDto.setRoles(roleDtos);
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto mapToDto(Account account) {
        Assert.notNull(account, "user is null");
        UserDto userDto = new UserDto();
        if (account.getUser() != null) {
            userDto = mapToDtoByRoleInfoAndOrgans(account.getUser(), account);
            userDto.setUsername(account.getUsername());
        } else {
            userDto.setName(account.getProviderName());
            userDto.setFamily(account.getProviderFamily());
            userDto.setFullName(account.getProviderName() + " " + account.getProviderFamily());
            userDto.setEmail(account.getProviderEmail());
        }
        if (SecurityUtils.getCurrentOrgan().isPresent()) {
            userDto.setCurOrgan(SecurityUtils.getCurrentOrgan().get());
        }

        return userDto;


    }


    @Override
    public SearchDto findAll(String search, Boolean filterOrgan, Pageable pageable) {

        Optional<Specification<User>> Specification = createSpecification(search, filterOrgan);
        Page<User> usersPage = findAll(Specification.get(), pageable);

        List<UserDto> content = usersPage.map(this::mapToDto).getContent();

        return new SearchDto.Builder().content(content)
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .currentPage(pageable.hashCode()).pageSize(pageable.getPageSize()).build();

    }


    public SearchDto listWithoutMemberRole(String search, Boolean filterOrgan, Pageable pageable, Long curModelVersion) {

        // Optional<Specification<User>> Specification = createSpecification(search, filterOrgan);
        Page<User> usersPage = userRepository.listWithoutMemberRole(pageable, search, curModelVersion, SecurityUtils.getCurrentOrganIdOrElseThrow());

        List<UserDto> content = usersPage.map(this::mapToDto).getContent();

        return new SearchDto.Builder().content(content)
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .currentPage(pageable.hashCode()).pageSize(pageable.getPageSize()).build();

    }

    public SearchDto listWithoutRole(String search, Boolean filterOrgan, Pageable pageable) {

        // Optional<Specification<User>> Specification = createSpecification(search, filterOrgan);
        Page<User> usersPage = userRepository.listWithoutRole(pageable, search, SecurityUtils.getCurrentOrganIdOrElseThrow());

        List<UserDto> content = usersPage.map(this::mapToDto).getContent();

        return new SearchDto.Builder().content(content)
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .currentPage(pageable.hashCode()).pageSize(pageable.getPageSize()).build();

    }

    @Override
    public List<User> findAllPostMember(Long poatId) {
        return this.getRepository().finaAllPostMember(SecurityUtils.getCurrentOrganIdOrElseThrow(), poatId);
    }
    @Override
    public List<User> findAllJobMember(Long jobId) {
        return this.getRepository().finaAllJobMember(SecurityUtils.getCurrentOrganIdOrElseThrow(),jobId);
    }
    @Override
    public List<User> findAllUnitMember(Long unitId) {
        return this.getRepository().finaAllUnitMember(SecurityUtils.getCurrentOrganIdOrElseThrow(),unitId);
    }
    private Optional<Specification<User>> createSpecification(String search, Boolean filterOrgan) {
        List<Specification<User>> specificationList = new ArrayList<>();


        specificationList.add((root, query, criteriaBuilder) -> {
            Join<User, UserOrgan> userOrganJoin = root.join(User_.USER_ORGANS);
            Join<UserOrgan, Organ> organJoin = userOrganJoin.join(UserOrgan_.ORGAN);
            Predicate[] predicates=new Predicate[1];

            predicates[0] = criteriaBuilder.equal(userOrganJoin.get(UserOrgan_.STATUS), Status.ACTIVE.name());
            if (filterOrgan)
               predicates= ArrayUtils.addAll(predicates, criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow()));


            if (StringUtils.hasText(search)) {
                List<Predicate> orPredicates = new ArrayList<>();

                orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.fullName)),
                        "%" + search.toLowerCase() + "%"));

                orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.mobile)),
                        "%" + search.toLowerCase() + "%"));


                orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.email)),
                        "%" + search.toLowerCase() + "%"));

                predicates=ArrayUtils.addAll(predicates,criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }


            query.distinct(true);
            return criteriaBuilder.and(predicates);
        });

        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    public Optional<UserDto> getUser(Principal principal) {
        Optional<UserDto> result = Optional.empty();
        if (principal == null) {
            return result;
        }
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            Object principalObject = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (principalObject instanceof HamsooUserDetail) {
                result = Optional.of(((HamsooUserDetail) principalObject).getUser());
            }
        }
        return result;
    }

    @Override
    public Optional<User> findOrganOwner(Long organId) {
        return this.getRepository().findOrganOwnerUser(organId, true);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long updateProfileImage(MultipartFile file, Long id) {
        FilesDto filesDto = filesService.map(file, Files.FileType.BYTE);
        User user = findByIdOrElseThrow(id);
        if (user.getImage() == null) {
            Files image = filesService.create(filesDto);
            filesDto.setId(image.getId());
            user.setImage(image);
            save(user);
        } else {
            filesDto.setId(user.getImage().getId());
            filesService.edit(filesDto);
        }
        return filesDto.getId();
    }

    @Override
    public FilesDto getProfileImage(Long id) {
        User user = findByIdOrElseThrow(id);
        if (user.getImage() == null) throw new BusinessException("error.user.imageFileIsNull");
        return filesService.getByteContent(user.getImage());

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User updateHamsooAccountByUsername(String username, UserDto userDto, MultipartFile image) {
        Optional<Account> accountOptional = accountService.findByUsername(username);
        if (accountOptional.isPresent()) {
            Account hamsooAccount;
            if (accountOptional.get().getProvider().getRegisterationId().equals(OauthProvider.HAMSOO)) {
                hamsooAccount = accountOptional.get();
            } else {
                hamsooAccount = accountService.findHamsooAccountByUserIdOrElseThrow(accountOptional.get().getUser().getId());
            }
            User user = this.findByIdOrElseThrow(hamsooAccount.getUser().getId());

            user.setName(userDto.getName());
            user.setFamily(userDto.getFamily());
            if (StringUtils.hasText(userDto.getMobile()))
                user.setMobile(userDto.getMobile());
            if (StringUtils.hasText(userDto.getEmail()))
                user.setEmail(userDto.getEmail());

            user = this.save(user);
            if (image != null && !image.isEmpty())
                updateProfileImage(image, user.getId());

            return user;


        } else {
            throw new BusinessException("error.curUser.notFound", username);
        }
    }

    @Override
    public User getHamsooAccountByUsername(String username) {
        Optional<Account> accountOptional = accountService.findByUsername(username);
        if (accountOptional.isPresent()) {
            Account hamsooAccount;
            if (accountOptional.get().getProvider().getRegisterationId().equals(OauthProvider.HAMSOO)) {
                hamsooAccount = accountOptional.get();
            } else {
                hamsooAccount = accountService.findHamsooAccountByUserIdOrElseThrow(accountOptional.get().getUser().getId());
            }
            return this.findByIdOrElseThrow(hamsooAccount.getUser().getId());
        } else {
            throw new BusinessException("error.curUser.notFound", username);
        }
    }

    @Override
    public boolean verifyUserMail(VerifyUserMailDto verifyUserMailDto) {
        Optional<User> user = this.getRepository().findByMailAndVerfiyCode(verifyUserMailDto.getMail(), verifyUserMailDto.getVerifyCode());
        if (user.isPresent()) {
            user.get().setMailVerify(true);
            this.save(user.get());
        }
        return user.isPresent();
    }

    @Override
    public Optional<User> findByAccountUsername(String username) {
        return this.getRepository().findByUsername(username);
    }


}
