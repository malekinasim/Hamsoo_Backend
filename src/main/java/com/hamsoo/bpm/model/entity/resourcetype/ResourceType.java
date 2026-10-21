package com.hamsoo.bpm.model.entity.resourcetype;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_resource_types", schema = "bpm", catalog = "BPM_DB")
public class ResourceType extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String description;
    private String status;
    private CodeCategory category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_type_id")
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

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    public CodeCategory getCategory() {
        return category;
    }

    public void setCategory(CodeCategory category) {
        this.category = category;
    }

    public enum Category {
        ORGAN_KNOWLEDGE("ORGAN_KNOWLEDGE"),
        INFRASTRUCTURE("INFRASTRUCTURE"),
        PERSONNEL("PERSONNEL");

        public static final String CATEGORY = "";

        private String code;

        Category(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
