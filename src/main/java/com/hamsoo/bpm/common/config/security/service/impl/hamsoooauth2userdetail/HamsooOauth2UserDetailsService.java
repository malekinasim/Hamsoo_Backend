package com.hamsoo.bpm.common.config.security.service.impl.hamsoooauth2userdetail;


import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.OAuth2UserInfo;
import com.hamsoo.bpm.common.exception.AuthenticationProcessingException;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import org.json.JSONObject;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class HamsooOauth2UserDetailsService extends DefaultOAuth2UserService {


    private AccountService accountService;
    private RoleMemberService roleMembersService;

    private OauthProviderService oauthProviderService;

    public HamsooOauth2UserDetailsService(AccountService accountService,
                                          RoleMemberService roleMembersService,
                                          OauthProviderService oauthProviderService) {
        this.accountService = accountService;
        this.roleMembersService = roleMembersService;
        this.oauthProviderService = oauthProviderService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            OAuth2User oAuth2User1 = processOAuth2User(userRequest, oAuth2User);
            if (oAuth2User1 == null)
                throw new OAuth2AuthenticationException("error.oauth2provider.usernamNotFound");
            return oAuth2User1;

        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes(),
                userRequest.getAdditionalParameters(), oauthProviderService);
        if (!StringUtils.hasText(oAuth2UserInfo.getUserName()) ) {
            throw new AuthenticationProcessingException("error.oauth2provider.usernamNotFound", userRequest.getClientRegistration().getRegistrationId());
        }
        Optional<Account> accountOptional = accountService.findByProviderAndProviderUsername(userRequest.getClientRegistration().getRegistrationId(), oAuth2UserInfo.getUserName() );

        AccountDto account = null;
        if (!accountOptional.isPresent()) {
            account = registerNewUser(userRequest, oAuth2UserInfo,oAuth2User.getAttributes());
            if (account != null) {
                return new HamsooUserDetail(account, oAuth2User.getAttributes(), null);
            }
            return null;
        } else {
            account = accountService.mapToDtoByPasswordAndUserAndRoles(accountOptional.get());
            if (account.getUserDto() != null) {
                UserDto user = account.getUserDto();

                Optional<OrganDto> organ = Optional.empty();
                if (userRequest.getClientRegistration().getRegistrationId().toUpperCase().equals(CommonOAuth2Provider.GOOGLE.name())) {
                    organ = user.getOrgans().stream().filter(OrganDto::getDefault).findFirst();

                } else {
                    //load current provider organ for user
                    organ = user.getOrgans().stream().filter(
                            org -> {
                                if (org != null && org.getOauthProviderDto() != null
                                        && org.getOauthProviderDto().getRegisterationId().equals(
                                        userRequest.getClientRegistration().getRegistrationId()
                                )) {
                                    return true;
                                }
                                return false;

                            }
                    ).findFirst();
                }
                if (organ.isPresent()) {
                    account.getUserDto().setCurOrgan(organ.get());
                    List<RoleMember> roleMembers = roleMembersService.findAllByUserAndOrgan(account.getUserDto().getId(), organ.get().getId());
                    roleMembers.addAll(roleMembersService.findAllAppBasedRoleByUser(organ.get().getId()));
                }

                return new HamsooUserDetail(account, oAuth2User.getAttributes(),
                        account.getUserDto(), organ.orElse(null));
            }
            return new HamsooUserDetail(account, oAuth2User.getAttributes(), null);
        }


    }

    private AccountDto registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo, Map<String, Object> attributes) {
        OauthProvider oauthProvider = oauthProviderService.findByRegisterationId(userRequest.getClientRegistration().getRegistrationId());
        if (oauthProvider != null) {
            Account account = new Account();
            account.setProvider(oauthProvider);
            account.setProviderUsername(oAuth2UserInfo.getUserName());
            account.setUsername(account.getProviderUsername() + "_" + userRequest.getClientRegistration().getRegistrationId());
            account.setPassword(account.getProviderUsername() + "_" + userRequest.getClientRegistration().getRegistrationId());
            account.setProviderName(oAuth2UserInfo.getName());
            account.setProviderFamily(oAuth2UserInfo.getFamily());
            account.setProviderEmail(oAuth2UserInfo.getEmail());
            account.setEnabled(true);
            account.setOidcUserInfo(new JSONObject(attributes).toString());

            account = accountService.save(account);
            return accountService.mapToDtoByPasswordAndUserAndRoles(account);
        }
        return null;
    }


}
