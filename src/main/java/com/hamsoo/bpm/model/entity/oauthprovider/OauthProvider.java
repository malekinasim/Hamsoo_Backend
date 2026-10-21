package com.hamsoo.bpm.model.entity.oauthprovider;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "bpm_oauth_providers", schema = "bpm", catalog = "BPM_DB")
public class OauthProvider extends AuditEntity<Long> {
    public static final String HAMSOO = "HAMSOO";

    private String issuerUri;
    private String tokenUri;
    private String authorizationUri;
    private String jwkSetUri;
    private String userInfoUri;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationGrantType;
    private String clientAuthenticationMethod;
    private String scope;
    private String usernameAttr;
    private String nameAttr;
    private String familyeAttr;
    private String mailAttr;
    private String registerationId;
    private Long id;
    private Organ organ;
    private Set<Account> accounts;
    private String type;
    private String ldapUrl;
    private String ldapBaseDn;
    private String ldapManagerDn;
    private String ldapManagerPassword;
    private Boolean isSAHABased;
    private String searchFilter;


    @Id
    @Column(name = "provider_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "issuer_uri")
    public String getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    @Basic
    @Column(name = "token_uri")
    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    @Basic
    @Column(name = "authorization_uri")
    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    @Basic
    @Column(name = "jwk_set_uri")
    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    @Basic
    @Column(name = "user_info_uri")
    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    @Basic
    @Column(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Basic
    @Column(name = "client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Basic
    @Column(name = "redirect_uri")
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Basic
    @Column(name = "authorization_grant_type")
    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    @Basic
    @Column(name = "client_authentication_method")
    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethod(String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    @Basic
    @Column(name = "scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Basic
    @Column(name = "registeration_id")
    public String getRegisterationId() {
        return registerationId;
    }

    public void setRegisterationId(String registerationId) {
        this.registerationId = registerationId;
    }

    @Basic
    @Column(name = "username_attr")
    public String getUsernameAttr() {
        return usernameAttr;
    }

    public void setUsernameAttr(String usernameAttr) {
        this.usernameAttr = usernameAttr;
    }

    @Basic
    @Column(name = "name_attr")
    public String getNameAttr() {
        return nameAttr;
    }

    public void setNameAttr(String nameAttr) {
        this.nameAttr = nameAttr;
    }

    @Basic
    @Column(name = "family_attr")
    public String getFamilyeAttr() {
        return familyeAttr;
    }

    public void setFamilyeAttr(String familyeAttr) {
        this.familyeAttr = familyeAttr;
    }

    @Basic
    @Column(name = "mail_attr")
    public String getMailAttr() {
        return mailAttr;
    }

    public void setMailAttr(String mailAttr) {
        this.mailAttr = mailAttr;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organ_id", referencedColumnName = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @OneToMany(mappedBy = "provider")
    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @Basic
    @Column(name = "is_saha_based", nullable = false)
    public Boolean getSAHABased() {
        return isSAHABased;
    }

    public void setSAHABased(Boolean SAHABased) {
        isSAHABased = SAHABased;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Basic
    @Column(name = "ldap_url", nullable = false)
    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }
    @Basic
    @Column(name = "ldap_base_dn", nullable = false)
    public String getLdapBaseDn() {
        return ldapBaseDn;
    }

    public void setLdapBaseDn(String ldapBaseDn) {
        this.ldapBaseDn = ldapBaseDn;
    }
    @Basic
    @Column(name = "ldap_manager_dn", nullable = false)
    public String getLdapManagerDn() {
        return ldapManagerDn;
    }

    public void setLdapManagerDn(String ldapManagerDn) {
        this.ldapManagerDn = ldapManagerDn;
    }
    @Basic
    @Column(name = "ldap_manager_password", nullable = false)
    public String getLdapManagerPassword() {
        return ldapManagerPassword;
    }

    public void setLdapManagerPassword(String ldapManagerPassword) {
        this.ldapManagerPassword = ldapManagerPassword;
    }

    @Basic
    @Column(name = "search_filter", nullable = false)
    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public enum providerType{
        DAO,LDAP,OIDC
    }
}
