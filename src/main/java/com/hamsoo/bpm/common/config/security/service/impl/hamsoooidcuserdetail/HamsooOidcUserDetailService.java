package com.hamsoo.bpm.common.config.security.service.impl.hamsoooidcuserdetail;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.exception.AuthenticationProcessingException;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.json.JSONObject;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class HamsooOidcUserDetailService extends OidcUserService {
    private AccountService accountService;
    private OauthProviderService oauthProviderService;
    private OrganService organService;

    public HamsooOidcUserDetailService(AccountService accountService, OrganService organService, OauthProviderService oauthProviderService) {
        this.accountService = accountService;
        this.oauthProviderService = oauthProviderService;
        this.organService = organService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        //  OAuth2AccessToken accessToken = userRequest.getAccessToken();
        OidcIdToken idToken = oidcUser.getIdToken();
        OidcUserInfo userInfo = oidcUser.getUserInfo();
        if (userInfo == null) {
            userInfo = new OidcUserInfo(oidcUser.getClaims());
        }
        if (!StringUtils.hasText(userInfo.getPreferredUsername()) || !StringUtils.hasText(userInfo.getClaim("sub"))) {
            throw new AuthenticationProcessingException("error.oidcprovider.usernamenotfound", userRequest.getClientRegistration().getRegistrationId());
        }
        Optional<Account> accountOptional = accountService.findByProviderAndProviderUsername(
                userRequest.getClientRegistration().getRegistrationId(), oidcUser.getPreferredUsername() == null ?
                        userInfo.getClaim("sub") + "_" +
                                userRequest.getClientRegistration().getRegistrationId() :
                        oidcUser.getPreferredUsername() + "_" +
                                userRequest.getClientRegistration().getRegistrationId());
        AccountDto account = null;
        if (!accountOptional.isPresent()) {
            account = registerNewAccount(userRequest, userInfo);
            return new HamsooUserDetail(account, oidcUser.getAttributes(), idToken, userInfo, null);
        } else {
            account = accountService.mapToDtoByPasswordAndUserAndRoles(accountOptional.get());
            if (account.getUserDto() != null) {
                Optional<Organ> organ = organService.getDefualtUserOrgan(account.getUsername());
                Optional<OrganDto> organDto = Optional.empty();
                if (organ.isPresent())
                    organDto = Optional.of(organService.mapToDto(organ.get()));

                return new HamsooUserDetail(account, oidcUser.getAttributes(), idToken, userInfo,
                        account.getUserDto(), organDto.orElse(null));
            }
            return new HamsooUserDetail(account, oidcUser.getAttributes(), idToken, userInfo, null);
        }


    }

    private AccountDto registerNewAccount(OAuth2UserRequest userRequest, OidcUserInfo oidcUserInfo) {
        OauthProvider oauthProvider = oauthProviderService.findByRegisterationId(userRequest.getClientRegistration().getRegistrationId());
        if (oauthProvider != null) {
            Account account = new Account();

            account.setProvider(oauthProvider);
            account.setProviderUsername(oidcUserInfo.getPreferredUsername() == null ? oidcUserInfo.getClaim("sub") + "_" + userRequest.getClientRegistration().getRegistrationId() : oidcUserInfo.getPreferredUsername() + "_" + userRequest.getClientRegistration().getRegistrationId());
            account.setPassword(oidcUserInfo.getPreferredUsername() == null ? oidcUserInfo.getClaim("sub") + "_" + userRequest.getClientRegistration().getRegistrationId() : oidcUserInfo.getPreferredUsername() + "_" + userRequest.getClientRegistration().getRegistrationId());
            account.setUsername(account.getProviderUsername().toUpperCase());
            account.setProviderName(oidcUserInfo.getGivenName());
            account.setProviderFamily(oidcUserInfo.getFamilyName());
            account.setProviderEmail(oidcUserInfo.getEmail());
            account.setEnabled(true);
            account.setOidcUserInfo(new JSONObject(oidcUserInfo.getClaims()).toString());
            // add another fields for saved saha based info
            if (oauthProvider.getSAHABased()) {
                if (oidcUserInfo.getClaim("PersianFullName") != null) {
                    account.setProviderName(oidcUserInfo.getClaim("PersianFullName"));
                }
            }
            account = accountService.save(account);
            return accountService.mapToDtoByPasswordAndUserAndRoles(account);
        }
        return null;
    }

}

