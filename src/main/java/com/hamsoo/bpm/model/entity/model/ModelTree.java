package com.hamsoo.bpm.model.entity.model;


import com.google.errorprone.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Immutable
@Table(name = "bpm_model_tree_viw", schema = "bpm", catalog = "BPM_DB")
public class ModelTree {

    private Long id;

    private Long modelId;
    private Long parentId;
    private String name;
    private String state;
    private String asisTobeCode;
    private String asisTobeName;
    private String statusCode;
    private String statusName;
    private String typeCode;
    private String typeName;
    private String processCategoryName;
    private Long organId;
    private Integer recursionLevel;
    private List<ModelTree> children;


    @Id
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "model_id")
    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Basic
    @Column(name = "parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Basic
    @Column(name = "asis_tobe_code")
    public String getAsisTobeCode() {
        return asisTobeCode;
    }

    public void setAsisTobeCode(String asisTobeCode) {
        this.asisTobeCode = asisTobeCode;
    }

    @Basic
    @Column(name = "asis_tobe_name")
    public String getAsisTobeName() {
        return asisTobeName;
    }

    public void setAsisTobeName(String asisTobeName) {
        this.asisTobeName = asisTobeName;
    }

    @Basic
    @Column(name = "status_code")
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Basic
    @Column(name = "status_name")
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Basic
    @Column(name = "type_code")
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Basic
    @Column(name = "type_name")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Basic
    @Column(name = "process_category_name")
    public String getProcessCategoryName() {
        return processCategoryName;
    }

    public void setProcessCategoryName(String processCategoryName) {
        this.processCategoryName = processCategoryName;
    }

    @Basic
    @Column(name = "organ_id")
    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    @Basic
    @Column(name = "recursion_level")
    public Integer getRecursionLevel() {
        return recursionLevel;
    }

    public void setRecursionLevel(Integer recursionLevel) {
        this.recursionLevel = recursionLevel;
    }

    @Transient
    public List<ModelTree> getChildren() {
        return children;
    }

    public void setChildren(List<ModelTree> children) {
        this.children = children;
    }
}
