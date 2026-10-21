package com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail;

import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;


public class HamsooUserDetail implements OAuth2User, UserDetails, OidcUser, Serializable {

    @Hidden
    private UserDto user;
    @Hidden
    private OrganDto currentOrgan = null;
    @Hidden
    private AccountDto account;
    @Hidden
    private Map<String, Object> attributes;
    @Hidden
    private OidcUserInfo userInfo;
    @Hidden
    private OidcIdToken idToken;

    public HamsooUserDetail() {
    }

    public HamsooUserDetail(UserDto user, AccountDto account, OrganDto currentOrgan) {
        this.user = user;
        this.account = account;
        this.currentOrgan = currentOrgan;
    }

    public HamsooUserDetail(AccountDto account, Map<String, Object> attributes, UserDto user) {
        this.user = user;
        this.account = account;
        Map modifiable = new HashMap<>(attributes);
        this.setAttributes(modifiable);
    }

    public HamsooUserDetail(AccountDto account, Map<String, Object> attributes, UserDto user, OrganDto currentOrgan) {
        this(account, attributes, user);
        this.currentOrgan = currentOrgan;
    }

    public HamsooUserDetail(AccountDto account, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo, UserDto user) {
        this.account = account;
        this.user = user;
        this.attributes = (Map<String, Object>) attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    public HamsooUserDetail(AccountDto account, Map<String, Object> attributes, OidcIdToken idToken,
                            OidcUserInfo userInfo, UserDto user, OrganDto currentOrgan) {
        this(account, attributes, idToken, userInfo, user);
        this.currentOrgan = currentOrgan;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = (HashMap<String, Object>) attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //we just add organ based and app based role authority and organ authority and resource authority based on organ and app role
        if (user != null) {
            if (user.getRoles() != null) {
                for (RoleDto role : user.getRoles()) {
                    GrantedAuthority roleGrantedAuthority = new SimpleGrantedAuthority(role.getName());
                    grantedAuthorities.add(roleGrantedAuthority);
                    if (!CollectionUtils.isEmpty(role.getResourceRoleDtoList())) {
                        for (GrantedResourceDto resourceRoleDto : role.getResourceRoleDtoList()) {
                            if (grantedAuthorities.stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(resourceRoleDto.getResourceItemDto().getName()))) {
                                GrantedAuthority ResourcePermissionGrantedAuthority = new SimpleGrantedAuthority(resourceRoleDto.getResourceItemDto().getName());
                                grantedAuthorities.add(ResourcePermissionGrantedAuthority);
                            }
                        }
                    }
                }
            }
            if (currentOrgan != null) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ORG_" + currentOrgan.getName());
                grantedAuthorities.add(grantedAuthority);
            }

        }
        return grantedAuthorities;
    }


    @Override
    public String getName() {
        return user != null ? user.getName() + " " + user.getFamily() : account.getProviderName() + " " + account.getProviderFamily();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return account.isEnabled();
    }

    public UserDto getUser() {
        return user;
    }

    public AccountDto getAccount() {
        return account;
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {

        return idToken;
    }

    public OrganDto getCurrentOrgan() {
        return currentOrgan;
    }

    public void setCurrentOrgan(OrganDto currentOrgan) {
        this.currentOrgan = currentOrgan;
    }
}
