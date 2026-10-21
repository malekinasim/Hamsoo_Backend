package com.hamsoo.bpm.repository.jpa.grantedesource;

import com.hamsoo.bpm.model.entity.grantedresource.GrantedResource;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GrantedResourceRepository extends HamsooRepository<GrantedResource, Long> {

    @Query("select gr from GrantedResource gr" +
            " inner join gr.role r " +
            " inner join gr.resourceItem ri" +
            " left join ri.feature f" +
            " where r.id=:roleId and" +
            " gr.status=:status and" +
            " ((exists (" +
            " select sfp from  SalePlanFeature sfp" +
            " inner join sfp.salePlan sp" +
            " inner join sfp.feature f1" +
            " where f1.status=:status and f1.id=f.id and sp.id=:planId)) or f.id is null)")
    List<GrantedResource> findAllByRoleIdAndStatusAndLastOrganSalePlanId(Long roleId, String status, Long planId);

    List<GrantedResource> findAllByRole_IdAndStatus(Long roleId, String status);

    @Query("select gr from GrantedResource gr" +
            " inner join gr.modelRole r " +
            " inner join gr.resourceItem ri" +
            " left join ri.feature f" +
            " where r.id=:roleId and" +
            " gr.status=:status and" +
            " ((exists (" +
            " select sfp from  SalePlanFeature sfp" +
            " inner join sfp.salePlan sp" +
            " inner join sfp.feature f1" +
            " where f1.status=:status and f1.id=f.id and sp.id=:planId)) or f.id is null)")
    List<GrantedResource> findAllByModelRoleIdAndStatusAndLastOrganSalePlanId(Long roleId, String status, Long planId);

    List<GrantedResource> findAllByModelRole_IdAndStatus(Long roleId, String name);

    List<GrantedResource> findAllByResourceItem_IdAndStatus(Long resourceId, String status);
}
