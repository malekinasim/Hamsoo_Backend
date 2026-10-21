package com.hamsoo.bpm.model.entity.organ;

import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.files.Files;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bpm_organs", schema = "bpm", catalog = "BPM_DB")
public class Organ extends AuditEntity<Long> {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Files logo;
    private OauthProvider oauthProvider;
    private List<UserOrgan> userOrgans;
    private Set<RoleMember> roleMembers;
    private List<OrganPlan> organPlans;
    private List<Chart> charts;
    private OrganType organType = OrganType.PRIVATE;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organ_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(long organId) {
        this.id = organId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Basic
    @Column(name = "status", nullable = false, length = -1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "organ_type", nullable = false, length = -1)
    public OrganType getOrganType() {
        return organType;
    }

    public void setOrganType(OrganType organType) {
        this.organType = organType;
    }

    @OneToMany(mappedBy = "organ")
    public List<UserOrgan> getUserOrgans() {
        return userOrgans;
    }

    public void setUserOrgans(List<UserOrgan> userOrgans) {
        this.userOrgans = userOrgans;
    }


    @OneToMany(mappedBy = "organ", cascade = CascadeType.PERSIST)
    public Set<RoleMember> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(Set<RoleMember> roleMembers) {
        this.roleMembers = roleMembers;
    }

    @OneToMany(mappedBy = "organ" ,fetch = FetchType.LAZY)
    public List<OrganPlan> getOrganPlans() {
        return organPlans;
    }

    public void setOrganPlans(List<OrganPlan> organPlans) {
        this.organPlans = organPlans;
    }

    @OneToOne
    @JoinColumn(name = "organ_logo")
    public Files getLogo() {
        return logo;
    }

    public void setLogo(Files logo) {
        this.logo = logo;
    }

    @OneToOne(mappedBy = "organ")
    public OauthProvider getOauthProvider() {
        return oauthProvider;
    }


    public void setOauthProvider(OauthProvider oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    @OneToMany(mappedBy = "organ" ,fetch = FetchType.LAZY)
    public List<Chart> getCharts() {
        return charts;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    public enum OrganType {
        PRIVATE, ORGAN
    }
}
