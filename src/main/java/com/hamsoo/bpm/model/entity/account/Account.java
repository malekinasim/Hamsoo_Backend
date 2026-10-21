package com.hamsoo.bpm.model.entity.account;

import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@Entity
@Table(name = "bpm_accounts", schema = "bpm", catalog = "BPM_DB")
public class Account extends AuditEntity<Long> {
    private Long id;
    private String username;
    private String password;
    private OauthProvider provider;
    private String providerEmail;
    private String providerUsername;
    private String providerName;
    private String providerFamily;
    private boolean enabled;
    private String oidcUserInfo;

    private User user;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long accountId) {
        this.id = accountId;
    }

    @Basic
    @Column(name = "username", nullable = false, length = -1)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password", nullable = false, length = -1)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToOne
    @JoinColumn(name = "provider", referencedColumnName = "provider_id")
    public OauthProvider getProvider() {
        return provider;
    }

    public void setProvider(OauthProvider provider) {
        this.provider = provider;
    }

    @Basic
    @Column(name = "provider_email", nullable = true, length = -1)
    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String email) {
        this.providerEmail = email;
    }

    @Basic
    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Basic
    @Column(name = "provider_username", nullable = true, length = -1)

    public String getProviderUsername() {
        return providerUsername;
    }

    public void setProviderUsername(String providerUsername) {
        this.providerUsername = providerUsername;
    }


    @Basic
    @Column(name = "provider_family", nullable = true, length = -1)

    public String getProviderFamily() {
        return providerFamily;
    }

    public void setProviderFamily(String providerFamily) {
        this.providerFamily = providerFamily;
    }

    @Basic
    @Column(name = "provider_name", nullable = true, length = -1)

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }


    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "oidc_user_info", columnDefinition = "jsonb")
    @Type(type = "json")
    public String getOidcUserInfo() {
        return oidcUserInfo;
    }

    public void setOidcUserInfo(String oidcUserInfo) {
        this.oidcUserInfo = oidcUserInfo;
    }
}
