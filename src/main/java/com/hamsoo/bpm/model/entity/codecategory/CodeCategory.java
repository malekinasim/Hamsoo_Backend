package com.hamsoo.bpm.model.entity.codecategory;

import com.hamsoo.bpm.model.entity.base.TimestampEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_code_category", schema = "bpm", catalog = "BPM_DB")
public class CodeCategory extends TimestampEntity<Long> {

    private Long id;
    private String category;
    private String code;
    private String faName;
    private String enName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_category_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "category", nullable = false, length = -1)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Basic
    @Column(name = "code", nullable = false, length = -1)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "fa_name", nullable = false, length = -1)
    public String getFaName() {
        return faName;
    }

    public void setFaName(String faName) {
        this.faName = faName;
    }

    @Basic
    @Column(name = "en_name", nullable = false, length = -1)
    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }
}
