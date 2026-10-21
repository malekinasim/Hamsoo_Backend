package com.hamsoo.bpm.model.entity.formbuilder;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "bpm_form_schema_catalog", schema = "bpm", catalog = "BPM_DB")
public class FormSchemaCatalog extends AuditEntity<Long> {

    private Long id;
    private String jsonContent;
    private String name;

    private String status;

    private CodeCategory runStatus;

    private Long formVersion;

    private String code;

    private Organ organ;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_schema_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "form_schema")
    @Type(type = "json")
    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    @Column(name = "name", nullable = false)
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

    @Column(name = "form_version", nullable = false)
    public Long getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Long formVersion) {
        this.formVersion = formVersion;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_schema_status", nullable = false)
    public CodeCategory getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(CodeCategory runStatus) {
        this.runStatus = runStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    public enum Status {
        DRAFT("DRAFT"),
        PUBLISHED("PUBLISHED");

        public static final String CATEGORY = "FORM_SCHEMA_STATUS";
        private String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
