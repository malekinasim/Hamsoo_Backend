package com.hamsoo.bpm.service.impl.account;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.wrapper.SmsService;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.account.ChangePasswordDto;
import com.hamsoo.bpm.model.dto.jwt.ForgetPasswordDto;
import com.hamsoo.bpm.model.dto.signup.RegisterRequest;
import com.hamsoo.bpm.model.dto.signup.SignUpRequest;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.repository.jpa.account.AccountRepository;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.accounttoken.AccountTokenService;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.service.api.person.PersonService;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserOrganService userOrganService;
    private final UserService userService;
    private final RoleService roleService;
    private final PersonService personService;
    private final PostMemberService postMemberService;
    private final RoleMemberService roleMemberService;
    private final OauthProviderService oauthProviderService;
    private final PasswordEncoder passwordEncoder;
    private final AccountTokenService accountTokenService;
    private final SmsService smsService;


    public AccountServiceImpl(AccountRepository accountRepository,
                              RoleService roleService, PersonService personService,
                              PostMemberService postMemberService, OauthProviderService oauthProviderService,
                              @Lazy PasswordEncoder passwordEncoder, AccountTokenService
                                      accountTokenService, SmsService smsService,
                              UserOrganService userOrganService,
                              UserService userService, RoleMemberService roleMemberService) {
        this.accountRepository = accountRepository;
        this.roleService = roleService;
        this.personService = personService;
        this.postMemberService = postMemberService;
        this.oauthProviderService = oauthProviderService;
        this.passwordEncoder = passwordEncoder;
        this.accountTokenService = accountTokenService;
        this.smsService = smsService;
        this.userService = userService;
        this.roleMemberService = roleMemberService;
        this.userOrganService = userOrganService;
    }

    @Override
    public AccountRepository getRepository() {
        return accountRepository;
    }


    @Override
    public Account findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.account.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.account.idIsNotValid"));
    }

    @Override
    public Optional<Account> findByProviderAndProviderUsername(String provider, String userName) {
        try {
            return accountRepository.findByProvider_RegisterationIdAndProviderUsername(provider, userName);
        } catch (Exception e) {

            return Optional.empty();
        }
    }


    @Override
    public Optional<Account> findByUsername(String username) {
        try {
            return accountRepository.findByUsernameIgnoreCaseAndEnabled(username, true);

        } catch (Exception e) {

            return Optional.empty();
        }
    }

    @Override
    public Account findHamsooAccountByUserIdOrElseThrow(Long userId) {
        return getRepository().findByProvider_RegisterationIdAndUserIdAndEnabled(OauthProvider.HAMSOO, userId, true).orElseThrow(
                () -> new BusinessException("error.account.notValidHamsooAccount"));
    }

    @Override
    public Map<String, String> sendResetPassTokenSMS(String nationalCode) {
        if (!StringUtils.hasText(nationalCode)) {
            throw new BusinessException("error.resetPass.mobileNotValid");
        }
        Optional<Account> account = this.findHamsooAccountByNationalCode(nationalCode);
        if (account.isEmpty()) {
            throw new BusinessException("error.accountByMobile.notFound");
        }
        AccountToken accountToken = accountTokenService.generateToken(account.get(), AccountToken.TokenType.RESET_PASS, AccountToken.DestinationType.SMS);
        String token = accountToken.getToken();
        String[] numbers = {account.get().getUser().getMobile()};
        Map<String, String> params = new HashMap<>();
        params.put("fullName", account.get().getUser().getFullName());
        params.put("token", token);

        Boolean result = smsService.sendSMS(numbers, params, "/templates/sms/resetpass.template.vsl");
        if (!result)
            throw new BusinessException("error.sms.notSend");
        params.put("mobile", account.get().getUser().getMobile());
        return params;

    }

    @Override
    public AccountToken checkResetPassToken(String nationalcode, String mobile, String token) {
        if (!StringUtils.hasText(mobile)) {
            throw new BusinessException("error.resetPass.mobileNotValid");
        }
        if (!StringUtils.hasText(nationalcode)) {
            throw new BusinessException("error.resetPass.nationalCodeNotValid");
        }
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("error.resetPass.TokenNotValid");
        }
        Optional<Account> account = this.findHamsooAccountByMobileAndNatioanlcode(mobile, nationalcode);
        if (account.isEmpty())
            throw new BusinessException("error.accountByMobileAndNationalCode.notfound");

        return accountTokenService.validateToken(account.get(),
                AccountToken.TokenType.RESET_PASS,
                AccountToken.DestinationType.SMS,
                token, LocalDateTime.now());
    }

    @Override
    public Boolean ChangePass(ForgetPasswordDto changePass) {
        Optional<Account> account = this.findHamsooAccountByMobileAndNatioanlcode(changePass.getMobile(), changePass.getNationalcode());
        if (account.isEmpty())
            throw new BusinessException("error.accountByMobileAndNationalCode.notfound");
        account.get().setPassword(passwordEncoder.encode(changePass.getPassword()));
        save(account.get());
        return true;
    }

    @Override
    public String sendSignUpTokenSMS(String mobile, String nationalCode) {
        if (!StringUtils.hasText(mobile)) {
            throw new BusinessException("error.user.mobileIsNotBlank");
        }
        if (!StringUtils.hasText(nationalCode)) {
            throw new BusinessException("error.signup.nationalCodeNotBlank");
        }
        Optional<Account> account = this.findHamsooAccountByNationalCode(nationalCode);
        if (account.isPresent()) {
            throw new BusinessException("error.signup.account.exist");
        }
        AccountToken accountToken = accountTokenService.generateToken(
                AccountToken.TokenType.SIENGUP,
                AccountToken.DestinationType.SMS,
                mobile);
        String token = accountToken.getToken();
        String[] numbers = {mobile};
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        Boolean result = smsService.sendSMS(numbers, params, "/templates/sms/signup.template.vsl");
        if (!result)
            throw new BusinessException("error.sms.notSend");
        return token;
    }

    @Override
    public Map<String, Object> checkSignupCode(String mobile, String nationalCode, String code) {
        Map<String, Object> result = new HashMap<>();

        if (!StringUtils.hasText(mobile)) {
            throw new BusinessException("error.signup.mobileNotValid");
        }
        if (!StringUtils.hasText(code)) {
            throw new BusinessException("error.signup.codeNotValid");
        }
        if (!StringUtils.hasText(nationalCode)) {
            throw new BusinessException("error.signup.nationalCodeNotBlank");
        }


        AccountToken token = accountTokenService.validateToken(mobile,
                AccountToken.TokenType.SIENGUP,
                AccountToken.DestinationType.SMS,
                code, LocalDateTime.now());
        result.put("token", token);
        List<Account> accounts = this.findALLAccountByNationalCod(nationalCode);
        if (accounts != null && !accounts.isEmpty()) {
            result.put("user", accounts.get(0).getUser());
        }
        return result;
    }

    private Account getAccountObj(String username, String password) {
        // Creating user's account
        Account account = new Account();
        account.setUsername(username);
        account.setEnabled(true);
        account.setPassword(passwordEncoder.encode(password));
        OauthProvider provider = oauthProviderService.findByRegisterationId(OauthProvider.HAMSOO);
        if (provider == null) throw new BusinessException("error.oauth2provider.providerNotFound");
        account.setProvider(provider);
        return save(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account registerHamsooAccount(SignUpRequest signUpRequest) {
        Optional<Account> findAccount = accountRepository.findByProvider_RegisterationIdAndUsernameIgnoreCase(OauthProvider.HAMSOO, signUpRequest.getUsername());
        if (findAccount.isPresent()) throw new BusinessException("error.account.uniqUsername");
        // Creating user's account
        Account account = getAccountObj(signUpRequest.getUsername(), signUpRequest.getPassword());
        User user = null;
        if (signUpRequest.getUserId() != null && !signUpRequest.getUserId().equals(0L)) {
            Optional<User> optionalUser = userService.findById(signUpRequest.getUserId());
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                user.setName(signUpRequest.getName());
                user.setFamily(signUpRequest.getFamily());
                user.setEmail(signUpRequest.getMail());
            }
        } else {
            user = userService.getUserObj(signUpRequest);

            //create myWorkspace and add user to workspace
            account.setUser(user);
            account.setModifiedBy(user);
            account.setCreatedBy(user);
            UserOrgan userOrgan = userOrganService.getUserPrivateOrganObj(account, user);
            Set<UserOrgan> userOrgans = user.getUserOrgans() != null ? user.getUserOrgans() : new HashSet<>();
            userOrgans.add(userOrgan);
            user.setUserOrgans(userOrgans);


            //add default Roles to user for His Workspace organ
            List<RoleMember> roleMembers = roleMemberService.getDefaultRoleForPrivateWorkspace(userOrgan.getOrgan(), user, account);
            user.setRoleMembers(roleMembers);
        }


        List<Account> accounts = user.getBpmAccounts() != null ? user.getBpmAccounts() : new ArrayList<Account>();
        accounts.add(account);
        user.setBpmAccounts(accounts);

        try {
            userService.save(user);

            if (!user.getMailVerify()) {
                String code = UUID.randomUUID().toString();
                user.setMailVerifyCode(code);
                try {
                    userService.sendVerificationEmail(user, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            addUserToOrgan(user);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("error.signup.fail");

        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account registerUser(Long id, RegisterRequest signUpRequest) {
        try {
            // Creating user's account
            Account foundAccount = this.getById(id);
            User user = userService.getUserObj(signUpRequest);

            foundAccount.setUser(user);

            //for create my workspace oragan for user
            userOrganService.getUserPrivateOrganObj(foundAccount, user);
            addUserToOrgan(user);

            if (!user.getMailVerify()) {
                String code = UUID.randomUUID().toString();
                user.setMailVerifyCode(code);
                try {
                    userService.sendVerificationEmail(user, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (foundAccount.getProvider() != null && foundAccount.getProvider().getOrgan() != null) {
                List<UserOrganDto> providerOrganUsers = userOrganService.findAllUserOfOrgan(foundAccount.getProvider().getOrgan().getId());
                if (providerOrganUsers.stream().noneMatch(userOrganDto -> userOrganDto.getUserId().equals(user.getId()))) {
                    UserOrganDto userOrganDto = new UserOrganDto();
                    userOrganDto.setUserId(user.getId());
                    userOrganDto.setOrganId(foundAccount.getProvider().getOrgan().getId());
                    userOrganService.create(userOrganDto);
                }
            }
            // add another organ to user if natinal code is in person
            return foundAccount;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("error.signup.fail");

        }

    }

    @Override
    public Optional<Account> findHamsooAccountByUser(Long userId) {
        return accountRepository.findHamsooAccountByUserId(userId, OauthProvider.HAMSOO);
    }

    @Override
    public List<Account> findNonHamsooAccountByUser(Long userId) {
        return accountRepository.findNonHamsooAccountByUserId(userId, OauthProvider.HAMSOO);
    }

    @Override
    public AccountDto mapToDtoByPasswordAndUserAndRoles(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setEnabled(account.isEnabled());
        accountDto.setProviderEmail(account.getProviderEmail());
        accountDto.setProviderName(account.getProviderName());
        accountDto.setUsername(account.getUsername());
        if (account.getUser() != null)
            accountDto.setUserDto(userService.mapToDtoByRoleInfoAndOrgans(account.getUser(), account));
        accountDto.setProviderId(account.getProvider().getId());
        accountDto.setPassword(account.getPassword());
        return accountDto;
    }

    @Override
    public AccountDto mapToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setEnabled(account.isEnabled());
        accountDto.setProviderEmail(account.getProviderEmail());
        accountDto.setProviderName(account.getProviderName());
        accountDto.setUsername(account.getUsername());
        accountDto.setProviderId(account.getProvider().getId());
        accountDto.setPassword(account.getPassword());
        return accountDto;
    }

    private void addUserToOrgan(User user) {
        User findUser = userService.findByIdOrElseThrow(user.getId());
        Person person = personService.findByNationalCode(findUser.getNationalCode());
        if (person != null) {
            List<PostMember> postMemberList = postMemberService.findAllByPersonIdAndAllOrgan(person.getId());
            if (!postMemberList.isEmpty()) {
                postMemberList.stream().map(postMember ->
                        postMember.getPost().getUnit().getChart().getOrgan().getId()
                ).distinct().forEach(organId ->
                        {
                            UserOrganDto userOrganDto = new UserOrganDto();
                            userOrganDto.setOrganId(organId);
                            userOrganDto.setUserId(findUser.getId());
                            userOrganDto.setPersonId(person.getId());
                            userOrganService.create(userOrganDto);
                        }

                );
            }
        }

    }


    @Override
    public Account addUserToAccount(String mobile, String nationalCode, Long accountId) {
        if (accountId == null) throw new BusinessException("error.signup.account.exist");
        Account find = this.findByIdOrElseThrow(accountId);

        Optional<User> userOptional = userService.findByNationalCode(nationalCode);
        //TODO
        if (userOptional.isPresent()) {
            find.setUser(userOptional.get());
            this.save(find);

            if (find.getProvider() != null && find.getProvider().getOrgan() != null) {
                List<UserOrganDto> providerOrganUsers = userOrganService.findAllUserOfOrgan(find.getProvider().getOrgan().getId());
                if (providerOrganUsers.stream().noneMatch(userOrganDto -> userOrganDto.getUserId().equals(userOptional.get().getId()))) {
                    UserOrganDto userOrganDto = new UserOrganDto();
                    userOrganDto.setUserId(userOptional.get().getId());
                    userOrganDto.setOrganId(find.getProvider().getOrgan().getId());
                    userOrganService.create(userOrganDto);
                }
            }
        }
        return find;
    }

    private Optional<Account> findHamsooAccountByNationalCode(String nationalCode) {

        return getRepository().findHamsooAccountByNationalCode(OauthProvider.HAMSOO, nationalCode);
    }

    private List<Account> findALLAccountByNationalCod(String nationalCode) {
        return getRepository().findALLAccountByNationalCod(nationalCode);
    }

    private Optional<Account> findHamsooAccountByMobileAndNatioanlcode(String mobile, String nationalCode) {

        return getRepository().findByProvider_RegisterationIdAndUser_MobileAndUser_NationalCode(OauthProvider.HAMSOO, mobile, nationalCode);
    }

    @Override
    public Account ChangePassHamsooAccount(String username, ChangePasswordDto changePasswordDto) {
        Optional<Account> accountOptional = getRepository().findByUsernameIgnoreCaseAndEnabled(username, true);
        if (accountOptional.isPresent()) {
            Account account;

            if (accountOptional.get().getProvider().getRegisterationId().equals(OauthProvider.HAMSOO)) {
                account = accountOptional.get();
            } else {
                account = findHamsooAccountByUserIdOrElseThrow(accountOptional.get().getUser().getId());
            }
            if (passwordEncoder.matches(changePasswordDto.getOldPassword(), account.getPassword())) {
                account.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));
                return this.save(account);
            } else {
                throw new BusinessException("error.account.notValidOldPassword");
            }
        } else {
            throw new BusinessException("error.curUser.notFound", username);
        }

    }
}
