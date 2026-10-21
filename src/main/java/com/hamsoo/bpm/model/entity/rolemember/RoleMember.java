package com.hamsoo.bpm.model.entity.rolemember;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.user.User;

import javax.persistence.*;

@Entity
@Table(name = "bpm_role_members", schema = "bpm", catalog = "BPM_DB")
public class RoleMember extends AuditEntity<Long> {
    private String status = "ACTIVE";

    private Long id;
    private Role role;
    private User user;
    private Organ organ;

    public RoleMember() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_member_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "status", nullable = false, length = -1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @ManyToOne
    @JoinColumn(name = "organ_id", referencedColumnName = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }
}
