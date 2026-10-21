package com.hamsoo.bpm.model.entity.chart;

import com.google.errorprone.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Immutable
@Table(name = "bpm_chart_tree_viw", schema = "bpm", catalog = "BPM_DB")
public class ChartTree {
    private String id;
    private Long organId;
    //search return
    private Long chartId;
    private Long tableId;
    private String code;
    private String name;
    private Long jobId;
    private Long postId;
    private String entityType;
    private String parentId;
    private Long parentTableId;
    //search
    private String searchWord;
    private List<ChartTree> children;

    @Basic
    @Column(name = "organ_id")
    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    @Basic
    @Column(name = "chart_id")
    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "table_id")
    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
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
    @Column(name = "job_id")
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Basic
    @Column(name = "post_id")
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Basic
    @Column(name = "entity_type")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String type) {
        this.entityType = type;
    }

    @Basic
    @Column(name = "parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Transient
    public String getFullName() {
        if (code != null && name != null)
            return code.concat("-").concat(name);
        else if (code == null) return getName();
        else return getCode();
    }

    @Basic
    @Column(name = "parent_table_id")
    public Long getParentTableId() {
        return parentTableId;
    }

    public void setParentTableId(Long parentTableId) {
        this.parentTableId = parentTableId;
    }

    @Transient
    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    @Transient
    public List<ChartTree> getChildren() {
        return children;
    }

    public void setChildren(List<ChartTree> children) {
        this.children = children;
    }


}
