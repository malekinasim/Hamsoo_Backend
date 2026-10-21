package com.hamsoo.bpm.model.entity.role;

import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.TimestampEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "bpm_roles", schema = "bpm", catalog = "BPM_DB")
public class Role extends TimestampEntity<Long> {


    private Long id;
    private String name;
    private String description;
    private String status = Status.ACTIVE.name();
    private String RoleType;
    private Set<RoleMember> roleMembers;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "role_type", nullable = false, length = -1)
    public String getRoleType() {
        return RoleType;
    }

    public void setRoleType(String roleType) {
        RoleType = roleType;
    }

    @OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST})
    public Set<RoleMember> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(Set<RoleMember> roleMembers) {
        this.roleMembers = roleMembers;
    }

    public enum OrganRole {
        ADMIN_ROLE("WORKSPACE_ADMIN"), USER_ROLE("WORKSPACE_USER"), SUPERVISOR_ROLE("WORKSPACE_SUPERVISOR");

        private String code;

        OrganRole(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum ApplicationRole {
        HAMSOO_ADMIN("HAMSOO_ADMIN");

        private String code;

        ApplicationRole(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum Role_Type {
        ORGAN_BASED, APPLICATION_BASED
    }


}
