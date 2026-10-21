package com.hamsoo.bpm.model.entity.userorgan;

import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_user_organs", schema = "bpm", catalog = "BPM_DB")
public class UserOrgan extends AuditEntity<Long> {
    private Long id;
    private Organ organ;
    private User user;
    private Person person;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status = Status.ACTIVE.name();
    private Boolean aDefault = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_organ_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "is_default", nullable = false)
    public Boolean getaDefault() {
        return aDefault;
    }

    public void setaDefault(Boolean aDefault) {
        this.aDefault = aDefault;
    }

    @Basic
    @Column(name = "from_date", nullable = true)
    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Basic
    @Column(name = "to_date", nullable = true)
    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "organ_id", referencedColumnName = "organ_id", nullable = false)
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", nullable = true)
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
