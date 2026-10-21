package com.hamsoo.bpm.model.entity.grantedresource;

import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.entity.resourceitem.ResourceItem;
import com.hamsoo.bpm.model.entity.role.Role;

import javax.persistence.*;


@Entity
@Table(name = "bpm_granted_resources", schema = "bpm", catalog = "BPM_DB")
public class GrantedResource extends AuditEntity<Long> {
    private Long id;
    private ResourceItem resourceItem;
    private Role role;
    private ModelRole modelRole;
//    private CodeCategory permissionType;
    private String status = Status.ACTIVE.name();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "granted_resources_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "resource_id", nullable = false)
    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    public void setResourceItem(ResourceItem resourceItem) {
        this.resourceItem = resourceItem;
    }

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne
    @JoinColumn(name = "model_role_id", referencedColumnName = "model_role_id")
    public ModelRole getModelRole() {
        return modelRole;
    }

    public void setModelRole(ModelRole modelRole) {
        this.modelRole = modelRole;
    }

//    @ManyToOne
//    @JoinColumn(name = "permission_type")
//    public CodeCategory getPermissionType() {
//        return permissionType;
//    }
//    public void setPermissionType(CodeCategory permission_type) {
//        this.permissionType = permission_type;
//    }



}
