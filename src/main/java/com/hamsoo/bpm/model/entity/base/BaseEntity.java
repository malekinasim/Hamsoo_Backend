package com.hamsoo.bpm.model.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseEntity<ID> implements IdentityCapable<ID>, Serializable {

    private Long version;

    @JsonIgnore
    @Column(name = "VERSION", nullable = false)
    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        @SuppressWarnings("unchecked")
        BaseEntity<ID> e = (BaseEntity<ID>) o;
        return (this.getId().equals(e.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

}
