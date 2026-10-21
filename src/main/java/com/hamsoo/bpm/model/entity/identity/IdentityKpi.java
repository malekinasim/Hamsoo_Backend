package com.hamsoo.bpm.model.entity.identity;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.kpicatalog.KpiCatalog;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_identity_kpi", schema = "bpm")
public class IdentityKpi extends AuditEntity<Long> {

    private Long id;
    private ModelVersion modelVersion;
    private String status;
    private KpiCatalog kpiCatalog;


    private CodeCategory kpiEvlDuration;
    private CodeCategory kpiOptimalTrend;
    private CodeCategory kpiMonitorType;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_kpi_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
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
    @JoinColumn(name = "kpi_catalog_id")
    public KpiCatalog getKpiCatalog() {
        return kpiCatalog;
    }

    public void setKpiCatalog(KpiCatalog kpiCatalog) {
        this.kpiCatalog = kpiCatalog;
    }



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_evl_duration", nullable = false)
    public CodeCategory getKpiEvlDuration() {
        return kpiEvlDuration;
    }

    public void setKpiEvlDuration(CodeCategory kpiEvlDuration) {
        this.kpiEvlDuration = kpiEvlDuration;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_optimal_trend", nullable = false)
    public CodeCategory getKpiOptimalTrend() {
        return kpiOptimalTrend;
    }

    public void setKpiOptimalTrend(CodeCategory kpiOptimalTrend) {
        this.kpiOptimalTrend = kpiOptimalTrend;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_monitor_type", nullable = false)
    public CodeCategory getKpiMonitorType() {
        return kpiMonitorType;
    }

    public void setKpiMonitorType(CodeCategory kpiMonitorType) {
        this.kpiMonitorType = kpiMonitorType;
    }
    public enum MonitorType {
        MANUAL("MANUAL"),
        AUTO("AUTO");
        public static final String CATEGORY = "KPI_MONITOR_TYPE";
        private String code;

        MonitorType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum TrendType {
        DESC("DESC"),
        ASC("ASC"),
        KEEP_AVAILABLE_STATUS("MSQ");

        public static final String CATEGORY = "KPI_TREND_TYPE";
        private String code;

        TrendType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum EvlDurationType {
        DAILY("DAILY"),
        WEAKLY("WEAKLY"),
        MONTHLY("MONTHLY"),
        SEASONAL("SEASONAL"),
        YEARLY("YEARLY");

        public static final String CATEGORY = "KPI_EVL_DURATION_TYPE";
        private String code;

        EvlDurationType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


}
