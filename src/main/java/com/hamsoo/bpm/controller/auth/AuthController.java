package com.hamsoo.bpm.controller.auth;

import com.hamsoo.bpm.common.config.security.filter.HamsooSwitchUserFilter;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.Token;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.TokenRefreshException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.jwt.*;
import com.hamsoo.bpm.model.dto.signup.RegisterRequest;
import com.hamsoo.bpm.model.dto.signup.SignUpRequest;
import com.hamsoo.bpm.model.dto.signup.VerifyUserMailDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.refreshtoken.RefreshToken;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.grantedresource.GrantedResourceService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.refreshtoken.RefreshTokenService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${hamsoo.resetpass.token.sms.expiration}")
    long ResetPassSmsExpiration;


    @Value("${hamsoo.resetpass.page.expiration}")
    int ResetPassPageExpiration;

    @Value("${hamsoo.signup.token.sms.expiration}")
    long SignupSmsExpiration;

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RoleMemberService roleMemberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;
    private final OrganService organService;
    private final UserService userService;
    private final GrantedResourceService grantedResourceService;


    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          AccountService accountService,
                          RefreshTokenService refreshTokenService, RoleMemberService roleMemberService, OrganService organService, UserService userService, GrantedResourceService grantedResourceService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accountService = accountService;
        this.refreshTokenService = refreshTokenService;
        this.roleMemberService = roleMemberService;
        this.organService = organService;
        this.userService = userService;
        this.grantedResourceService = grantedResourceService;
    }

    //----------login---------
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody JwtRequest authenticationRequest) {
        Authentication authenticate = authenticate(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", ((UserDetails) authenticate.getPrincipal()).getUsername());
        // claims.put("principal", authenticate.getPrincipal());

        //TODO
        //dont use app role authority , organ role authority and resource authority in token generation
        // claims.put("authorities", authenticate.getAuthorities());
        Token token = jwtTokenProvider.issueToken(claims);
        Optional<Organ> organ = Optional.empty();
        if (authenticationRequest.getCurOrgan() != null) {
            organ = organService.findById(authenticationRequest.getCurOrgan());
        } else
            organ = organService.getDefualtUserOrgan(authenticationRequest.getUsername());
        if (organ.isEmpty()) throw new BusinessException("error.user.defaultOrganIdNotValid");

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(((HamsooUserDetail) authenticate.getPrincipal()).getAccount().getId());
        return new ResponseEntity<>(new JwtResponse(refreshToken.getDuration(), organ.get().getId(), token.getTokenValue(), refreshToken.getToken()), HttpStatus.OK);
    }


    //----------resfreshtoken---------
    @PreAuthorize("permitAll()")
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody JwtRefreshRequest body, HttpServletRequest request) {
        String requestRefreshToken = body.getRefreshToken();
        if (!StringUtils.hasText(requestRefreshToken))
            requestRefreshToken = request.getHeader("refreshToken");
        Long organ = body.getCurOrgan();
        if (StringUtils.hasText(requestRefreshToken)) {
            String finalRequestRefreshToken = requestRefreshToken;
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getAccount)
                    .map(account -> {
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("username", account.getUsername());
                        claims.put("authorities", getAuthorities(account, organ, body.getOriginalUser()));
                        Token token = jwtTokenProvider.issueToken(claims);
                        return new ResponseEntity<>(new JwtRefreshResponse(token, finalRequestRefreshToken), HttpStatus.OK);
                    })
                    .orElseThrow(() -> new TokenRefreshException("error.user.session.expire", finalRequestRefreshToken));
        } else {
            throw new TokenRefreshException("error.user.session.expire", requestRefreshToken);
        }
    }

    //----------valid---------
    @Deprecated
    @PostMapping(value = "/validjwt")
    public ResponseEntity<?> isValid(HttpServletRequest request) {
        String jwtToken = jwtTokenProvider.getJwtToken(request);
        if (StringUtils.hasText(jwtToken) && jwtTokenProvider.validateAndParseToken(jwtToken))
            return new ResponseEntity<>(HttpStatus.OK);

        String requestRefreshToken = request.getHeader("refreshToken");
        if (StringUtils.hasText(requestRefreshToken)) {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(refreshToken -> {
                        LocalDateTime expirDate = refreshToken.getExpiryDate();
                        Date now = new Date(System.currentTimeMillis());
                        Long maxAge = expirDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - now.getTime();
                        if (maxAge > 60000) {
                            return new ResponseEntity<>(HttpStatus.OK);
                        } else {
                            throw new TokenRefreshException("error.user.session.expire", requestRefreshToken);
                        }
                    })
                    .orElseThrow(() -> new TokenRefreshException("error.user.session.expire", requestRefreshToken));
        } else {
            throw new TokenRefreshException("error.user.session.expire", requestRefreshToken);
        }
    }

    //----------logoff---------
    @GetMapping(value = "/logoff")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> logoff(HttpServletRequest request) {
        try {
            String requestRefreshToken = request.getHeader("refreshToken");
            refreshTokenService.deleteByToken(requestRefreshToken);
        } catch (Exception e) {
            if (SecurityUtils.getCurrentAccount().isPresent())
                refreshTokenService.deleteAllExpiredByAccountId(SecurityUtils.getCurrentAccount().get().getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new BusinessException("error.user_disabled");
        } catch (BadCredentialsException e) {
            throw new BusinessException("error.invalid_credential");
        } catch (AuthenticationException e) {
            throw new BusinessException("error.invalid_credential");
        }
    }


    //----------resetpass sms token ---------
    @PostMapping("/sendsms/resetpass")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> resetPassword(@RequestBody ForgetPasswordDto changePass) {
        Map<String, String> response = accountService.sendResetPassTokenSMS(changePass.getNationalcode());
        if (response.containsKey("code") && !StringUtils.hasText(response.get("code")))
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return ResponseEntity.ok().body(new JSONObject().appendField("expire",
                (ResetPassSmsExpiration / 1000)).appendField("mobile", response.get("mobile"))
        );
    }

    //----------validate resepassword code---------
    @PostMapping("/validate/resetpasssms")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> checkResetPassToken(@RequestBody ForgetPasswordDto changePass) {

        AccountToken accountToken = accountService.checkResetPassToken(changePass.getNationalcode(), changePass.getMobile(), changePass.getCode());
        if (accountToken == null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    //----------change pass---------
    @PostMapping("/changepass")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> changePass(
            @Valid @RequestBody ForgetPasswordDto requestObj) {
        Boolean changeed = accountService.ChangePass(requestObj);
        if (!changeed) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    //----------signup---------
    @PostMapping("/registerUserInfo")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        String token = jwtTokenProvider.getJwtToken(request);
        if (StringUtils.hasText(token)) {
            if (jwtTokenProvider.validateAndParseToken(token)) {
                Account account = accountService.registerUser(SecurityUtils.getCurrentAccount().get().getId(), registerRequest);
                if (account != null && account.getUser() != null) {
                    return new ResponseEntity<>(account, HttpStatus.CREATED);
                }
            }
        }
        throw new BusinessException("error.signup.signupExpire");
    }

    //----------signup sms token ---------
    @PostMapping("/sendsms/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> sendSignupCode(@RequestBody SignUpRequest request) {
        String code = accountService.sendSignUpTokenSMS(request.getMobile(), request.getNationalCode());
        if (!StringUtils.hasText(code)) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return ResponseEntity.ok().body(new JSONObject().appendField("expire",
                (SignupSmsExpiration / 1000)));

    }

    //----------signup sms token ---------
    @GetMapping("/finduserinfo")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> findUserInfo(@RequestParam(name = "mobile") String mobile,
                                          @RequestParam(name = "natCode") String nationalCode,
                                          HttpServletRequest request) {

        String token = jwtTokenProvider.getJwtToken(request);
        if (StringUtils.hasText(token)) {
            if (jwtTokenProvider.validateAndParseToken(token)) {
                if (SecurityUtils.getCurrentUser().isPresent()) {
                    return new ResponseEntity<>(SecurityUtils.getCurrentAccount().get(), HttpStatus.OK);
                } else {
                    Account account = accountService.addUserToAccount(mobile, nationalCode, SecurityUtils.getCurrentAccount().get().getId());
                    return new ResponseEntity<>(userService.mapToDto(account), HttpStatus.OK);
                }
            }
        }
        throw new BusinessException("error.signup.signupExpire");

    }

    //----------validate resepassword code and return user if present---------
    @PostMapping("/validate/signupsms")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> checkSignupCode(
            @RequestBody SignUpRequest request) {

        if (request.getMobile() != null && StringUtils.hasText(request.getCode())) {
            Map<String, Object> map = accountService.checkSignupCode(
                    request.getMobile(), request.getNationalCode(),
                    request.getCode());
            if (map.get("token") == null) throw new BusinessException("error.code.notValid");

            return new ResponseEntity<>(map.get("user") != null ? userService.mapToDto((User) map.get("user")) : null,
                    HttpStatus.OK);
        }
        throw new BusinessException("error.code.notValid");
    }

    //----------signup---------
    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerHamsooUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        Account result = accountService.registerHamsooAccount(signUpRequest);
        if (result != null) {
            Authentication authenticate = authenticate(signUpRequest.getUsername(), signUpRequest.getPassword());
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", result.getUsername());
            claims.put("authorities", authenticate.getAuthorities());
            Token token = jwtTokenProvider.issueToken(claims);
            UserOrgan userOrgan = null;
            if (result.getUser().getUserOrgans() != null && !result.getUser().getUserOrgans().isEmpty()) {
                userOrgan = result.getUser().getUserOrgans().iterator().next();
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(((HamsooUserDetail) authenticate.getPrincipal()).getAccount().getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(refreshToken.getDuration(),
                    userOrgan != null ? userOrgan.getOrgan().getId() : null,
                    token.getTokenValue(), refreshToken.getToken()));
        } else
            throw new BusinessException("error.signup.fail");

    }

//    private Collection<? extends GrantedAuthority> getAuthoritiesOfCurOrganAndApp(Account account, Long curOrgan) {
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (account.getUser() != null) {
//            if (account.getUser().getRoleMembers() != null && !account.getUser().getRoleMembers().isEmpty()
//
//            ) {
//                for (RoleMember member : account.getUser().getRoleMembers()) {
//                    if (member.getOrgan() != null && member.getOrgan().getId().equals(curOrgan)) {
//                        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority( member.getRole().getName());
//                        grantedAuthorities.add(grantedAuthority);
//                        List<GrantedResourceDto> resourceDtos = grantedResourceService.findAllByRoleIdAndCurrentOrganSalePlan(member.getRole().getId());
//                        if (!CollectionUtils.isEmpty(resourceDtos)) {
//                            for (GrantedResourceDto resourceRoleDto : resourceDtos) {
//                                GrantedAuthority ResourcePermissionGrantedAuthority = new SimpleGrantedAuthority(resourceRoleDto.getResourceItemDto().getName());
//                                grantedAuthorities.add(ResourcePermissionGrantedAuthority);
//                            }
//                        }
//                    }
//
//                }
//            }
//            if (curOrgan != null) {
//                Optional<Organ> organ = organService.findById(curOrgan);
//                if (organ.isPresent()) {
//                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ORG_" + organ.get().getName());
//                    grantedAuthorities.add(grantedAuthority);
//                }
//
//            }
//            List<RoleMember> appBasedRoleMember = roleMemberService.findAllAppBasedRoleByUser(account.getUser().getId());
//            for (RoleMember member : appBasedRoleMember) {
//                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority( member.getRole().getName());
//                grantedAuthorities.add(grantedAuthority);
//                List<GrantedResourceDto> resourceDtos = grantedResourceService.findAllByRoleIdAndCurrentOrganSalePlan(member.getRole().getId());
//                if (!CollectionUtils.isEmpty(resourceDtos)) {
//                    for (GrantedResourceDto resourceRoleDto : resourceDtos) {
//                        GrantedAuthority ResourcePermissionGrantedAuthority = new SimpleGrantedAuthority(resourceRoleDto.getResourceItemDto().getName());
//                        grantedAuthorities.add(ResourcePermissionGrantedAuthority);
//                    }
//                }
//
//            }
//        }
//        return grantedAuthorities;
//    }

    private Collection<? extends GrantedAuthority> getAuthorities(Account account, Long curOrgan, String originalUser) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (account.getUser() != null) {
            //TODO
            //dont use app role authority , organ role authority and resource authority in token generation
            // grantedAuthorities.addAll(getAuthoritiesOfCurOrganAndApp(account, curOrgan));

            if (originalUser != null) {
                Optional<Account> orgAccount = accountService.findByUsername(originalUser);
                if (orgAccount.isEmpty())
                    throw new BusinessException("error.userSwitch.session.expire");
                List<RoleMember> roleMembers = roleMemberService.findAllAppBasedRoleByUser(orgAccount.get().getUser().getId());
                // roleMembers.addAll(roleMemberService.findAllOrganBasedRoleByUser(orgAccount.get().getUser().getId()));
                if (roleMembers.stream().noneMatch(roleMember -> roleMember.getRole().getName().equals(Role.ApplicationRole.HAMSOO_ADMIN.getCode())))
                    throw new BusinessException("error.userSwitch.session.expire");
                Optional<Organ> originalUserCurOrg = organService.getDefualtUserOrgan(originalUser);
                if (originalUserCurOrg.isEmpty())
                    throw new BusinessException("error.userSwitch.session.expire");

                List<GrantedAuthority> originalAuthorities = new ArrayList<>();
                //TODO
                //dont use app role authority , organ role authority and resource authority in token generation
                // originalAuthorities.addAll(getAuthoritiesOfCurOrganAndApp(orgAccount.get(), originalUserCurOrg.get().getId()));


                UserDetails orignaluserDetails = new org.springframework.security.core.userdetails.User(originalUser, account.getUsername(), originalAuthorities);
                SwitchUserGrantedAuthority sAuth = new SwitchUserGrantedAuthority(
                        HamsooSwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR,
                        new UsernamePasswordAuthenticationToken(orignaluserDetails, "", originalAuthorities)
                );
                grantedAuthorities.add(sAuth);


            }
        }
        return grantedAuthorities;
    }

    @PostMapping("/verifyMail")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> verifyUserMail(@RequestBody @Valid VerifyUserMailDto verifyUserMailDto) {
        if (userService.verifyUserMail(verifyUserMailDto))
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        else
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
    }
}
