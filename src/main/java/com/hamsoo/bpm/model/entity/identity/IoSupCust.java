package com.hamsoo.bpm.model.entity.identity;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_identity_io_sup_custs", schema = "bpm")
public class IoSupCust extends AuditEntity<Long> {

    private Long id;
    private CodeCategory type;
    private ModelVersion internalSupCust;
    private ExternalProcess externalSupCust;
    private String eventSupCust;
    private String status;
    private IdentityIo identityIo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sup_cust_id")
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
    @JoinColumn(name = "internal_sup_cust")
    public ModelVersion getInternalSupCust() {
        return internalSupCust;
    }

    public void setInternalSupCust(ModelVersion internalSupCust) {
        this.internalSupCust = internalSupCust;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_sup_cust")
    public ExternalProcess getExternalSupCust() {
        return externalSupCust;
    }

    public void setExternalSupCust(ExternalProcess externalSupCust) {
        this.externalSupCust = externalSupCust;
    }

    @Basic
    @Column(name = "event_sup_cust")
    public String getEventSupCust() {
        return eventSupCust;
    }

    public void setEventSupCust(String eventSupCust) {
        this.eventSupCust = eventSupCust;
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
    @JoinColumn(name = "identity_io_id")
    public IdentityIo getIdentityIo() {
        return identityIo;
    }

    public void setIdentityIo(IdentityIo identityIo) {
        this.identityIo = identityIo;
    }

    public enum Type {
        INTERNAL_PROCESS("IN_PRC"),
        EXTERNAL_PROCESS("EX_PRC"),
        EVENT("EVENT");

        public static final String CATEGORY = "IO_SUP_CUST_TYPE";
        private String code;

        Type(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
