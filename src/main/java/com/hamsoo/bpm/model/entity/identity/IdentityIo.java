package com.hamsoo.bpm.model.entity.identity;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_identity_io", schema = "bpm")
public class IdentityIo extends AuditEntity<Long> {

    private Long id;
    private CodeCategory type;
    private IoCatalog ioCatalog;
    private String status;
    private ModelVersion modelVersion;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_io_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @JoinColumn(name = "io_catalog_id")
    public IoCatalog getIoCatalog() {
        return ioCatalog;
    }

    public void setIoCatalog(IoCatalog ioCatalog) {
        this.ioCatalog = ioCatalog;
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
    @JoinColumn(name = "model_version_id")
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    public enum Type {
        INPUT("INPUT"),
        OUTPUT("OUTPUT");

        public static final String CATEGORY = "IDENTITY_IO_TYPE";
        private String code;

        Type(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
