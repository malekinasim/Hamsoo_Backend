package com.hamsoo.bpm.model.entity.user;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.files.Files;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bpm_users", schema = "bpm", catalog = "BPM_DB")
public class User extends AuditEntity<Long> {
    private Long id;

    private String name;
    private String family;
    private String mobile;
    private String fullName;
    private String email;
    private String nationalCode;
    private Boolean completed = false;
    private Boolean mailVerify = false;
    private String mailVerifyCode;
    private Set<NotificationAudience> notificationAudiences;
    private List<RoleMember> roleMembers;
    private Set<UserOrgan> userOrgans;
    private List<Account> bpmAccounts;
    private Files image;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(long userId) {
        this.id = userId;
    }

    @Basic
    @Column(name = "name", nullable = true, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "family", nullable = true, length = -1)
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }


    @Basic
    @Column(name = "mobile", length = -1)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Basic
    @Column(name = "completed", nullable = false)
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Basic
    @Column(name = "national_code", nullable = false)
    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Basic
    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "mail_Verify", nullable = false)
    public Boolean getMailVerify() {
        return mailVerify;
    }

    public void setMailVerify(Boolean mailVerify) {
        this.mailVerify = mailVerify;
    }

    @Basic
    @Column(name = "mail_Verify_Code", nullable = false, length = -1)
    public String getMailVerifyCode() {
        return mailVerifyCode;
    }

    public void setMailVerifyCode(String mailVerifyCode) {
        this.mailVerifyCode = mailVerifyCode;
    }


    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
    public Set<UserOrgan> getUserOrgans() {
        return userOrgans;
    }

    public void setUserOrgans(Set<UserOrgan> userOrgans) {
        this.userOrgans = userOrgans;
    }

//    @OneToMany(mappedBy = "user")
//    public List<ModelApprover> getModelApprovers() {
//        return modelApprovers;
//    }
//
//    public void setModelApprovers(List<ModelApprover> modelApprovers) {
//        this.modelApprovers = modelApprovers;
//    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<NotificationAudience> getNotificationAudiences() {
        return notificationAudiences;
    }

    public void setNotificationAudiences(Set<NotificationAudience> notificationAudiences) {
        this.notificationAudiences = notificationAudiences;
    }

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    public List<RoleMember> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(List<RoleMember> roleMembers) {
        this.roleMembers = roleMembers;
    }


    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    public List<Account> getBpmAccounts() {
        return bpmAccounts;
    }

    public void setBpmAccounts(List<Account> bpmAccounts) {
        this.bpmAccounts = bpmAccounts;
    }

    @Formula(value = "concat(name,' ',family )")
    public String getFullName() {
        return fullName;
    }

    @ManyToOne
    @JoinColumn(name = "image_id")
    public Files getImage() {
        return image;
    }

    public void setImage(Files image) {
        this.image = image;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
