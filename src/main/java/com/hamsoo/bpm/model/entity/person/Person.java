package com.hamsoo.bpm.model.entity.person;

import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "bpm_persons", schema = "bpm", catalog = "BPM_DB")
public class Person extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String family;
    private String status;
    private String nationalCode;
    private String personalCode;
    private String fullName;

    private List<PostMember> postMemberList;
    private List<UserOrgan> userOrgans;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "family", nullable = false)
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    @Column(name = "national_code", nullable = false)
    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    @Column(name = "personal_code", nullable = false)
    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Formula(value = "concat(name,' ',family )")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    public List<PostMember> getPostMemberList() {
        return postMemberList;
    }

    public void setPostMemberList(List<PostMember> postMemberList) {
        this.postMemberList = postMemberList;
    }

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    public List<UserOrgan> getUserOrgans() {
        return userOrgans;
    }

    public void setUserOrgans(List<UserOrgan> userOrgans) {
        this.userOrgans = userOrgans;
    }
}
