package com.hamsoo.bpm.model.entity.modelrole;

import com.hamsoo.bpm.model.entity.base.TimestampEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_model_roles", schema = "bpm", catalog = "BPM_DB")
public class ModelRole extends TimestampEntity<Long> {

    private Long id;
    private String code;
    private String name;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_role_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public enum Code {
        MODEL_EDITOR("MODEL_EDITOR"),
        MODEL_VIEWER("MODEL_VIEWER"),
        MODEL_APPROVER("MODEL_APPROVER"),
        MODEL_OWNER("MODEL_OWNER");

        private String code;

        Code(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
