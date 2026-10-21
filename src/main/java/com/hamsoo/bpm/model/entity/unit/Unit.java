package com.hamsoo.bpm.model.entity.unit;

import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.post.Post;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bpm_units", schema = "bpm", catalog = "BPM_DB")
public class Unit extends AuditEntity<Long> {

    private Long id;
    private String name;
    private Chart chart;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String status;
    private String sourceUnitId;
    private List<UnitCodeHistory> unitCodeHistoryList;
    private Unit parentUnit;
    private List<Post> posts;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
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
    @JoinColumn(name = "chart_id")
    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
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

    @Basic
    @Column(name = "source_unit_id")
    public String getSourceUnitId() {
        return sourceUnitId;
    }

    public void setSourceUnitId(String sourceUnitId) {
        this.sourceUnitId = sourceUnitId;
    }

    @ManyToOne
    @JoinColumn(name = "parent_unit_id")
    public Unit getParentUnit() {
        return parentUnit;
    }

    public void setParentUnit(Unit parentUnit) {
        this.parentUnit = parentUnit;
    }

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    public List<UnitCodeHistory> getUnitCodeHistoryList() {
        return unitCodeHistoryList;
    }

    public void setUnitCodeHistoryList(List<UnitCodeHistory> unitCodeHistoryList) {
        this.unitCodeHistoryList = unitCodeHistoryList;
    }
    @OneToMany(mappedBy = "unit" ,fetch = FetchType.LAZY)
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }


}
