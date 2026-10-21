package com.hamsoo.bpm.model.entity.post;

import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.job.Job;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bpm_posts", schema = "bpm", catalog = "BPM_DB")
public class Post extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String description;
    private String code;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String status;
    private Unit unit;
    private Job job;
    private List<PostMember> postMembers;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false)
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
    @Column(name = "code")

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "valid_from")
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    @Basic
    @Column(name = "valid_to")
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "job_id")
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @ManyToOne
    @JoinColumn(name = "unit_id")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }


    @OneToMany(mappedBy = "post" ,fetch = FetchType.LAZY)
    public List<PostMember> getPostMembers() {
        return postMembers;
    }

    public void setPostMembers(List<PostMember> postMembers) {
        this.postMembers = postMembers;
    }
}
