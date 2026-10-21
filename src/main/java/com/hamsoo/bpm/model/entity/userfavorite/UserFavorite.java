package com.hamsoo.bpm.model.entity.userfavorite;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.user.User;

import javax.persistence.*;

@Entity
@Table(name = "bpm_user_favorites", schema = "bpm", catalog = "BPM_DB")
public class UserFavorite extends AuditEntity<Long> {

    private Long id;
    private String referenceType;
    private Long referenceId;
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "refrence_type")
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    @Basic
    @Column(name = "refrece_id")
    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum ReferenceType {
        MODEL;
    }
}
