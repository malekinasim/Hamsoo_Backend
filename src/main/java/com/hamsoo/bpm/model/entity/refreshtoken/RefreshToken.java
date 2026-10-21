package com.hamsoo.bpm.model.entity.refreshtoken;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.base.TimestampEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_refreshtokens", schema = "bpm", catalog = "BPM_DB")
public class RefreshToken extends TimestampEntity<Long> {

    private Long id;
    private Account account;
    private String token;
    private LocalDateTime expiryDate;
    private Long duration;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Column(nullable = false, unique = true)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(nullable = false)
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Transient
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }


}