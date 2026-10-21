package com.hamsoo.bpm.model.entity.form;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_form_lov_catalogs", schema = "bpm", catalog = "BPM_DB")
public class FormLovCatalog extends AuditEntity<Long> {

    private Long id;
    private String name;
    private CodeCategory type;
    private Organ organ;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_lov_catalog_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    public CodeCategory getType() {
        return type;
    }

    public void setType(CodeCategory type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public enum Type {
        FUNCTION_TYPE("FUNCTION_TYPE"),
        FUNCTION_USAGE_STATUS("FUNCTION_USAGE_STATUS"),
        RULE_TYPE("RULE_TYPE"),
        ENTITY_TYPE("ENTITY_TYPE"),
        TASK_PUBLISH_STATUS("TASK_PUBLISH_STATUS");

        public static final String CATEGORY = "TASK_CATALOG_TYPE";

        private String code;

        Type(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
