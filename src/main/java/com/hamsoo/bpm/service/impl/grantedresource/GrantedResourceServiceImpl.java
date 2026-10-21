package com.hamsoo.bpm.service.impl.grantedresource;

import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.entity.grantedresource.GrantedResource;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.service.api.grantedresource.GrantedResourceService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.resourceitem.ResourceItemService;
import com.hamsoo.bpm.repository.jpa.grantedesource.GrantedResourceRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GrantedResourceServiceImpl implements GrantedResourceService {
    private GrantedResourceRepository grantedResourceRepository;
    private ResourceItemService resourceItemService;
    private OrganPlanService organPlanService;

    public GrantedResourceServiceImpl(GrantedResourceRepository grantedResourceRepository, @Lazy ResourceItemService resourceItemService, @Lazy OrganPlanService organPlanService) {
        this.grantedResourceRepository = grantedResourceRepository;
        this.resourceItemService = resourceItemService;
        this.organPlanService = organPlanService;
    }

    @Override
    public GrantedResourceRepository getRepository() {
        return grantedResourceRepository;
    }

    @Override
    public List<GrantedResourceDto> findAllByRoleIdAndCurrentOrganSalePlan(Long roleId, Long OrganId) {
        List<GrantedResource> roleResources = new ArrayList<>();
        if (OrganId != null) {
            Optional<OrganPlan> organPlan = organPlanService.getLastOrganSalePlan(OrganId);
            if (organPlan.isPresent() && organPlan.get().getSalePlan() != null) {
                //dont check expire of last organPlan because we add expire plan to all organ that it's last Organ sale plan is expired
                roleResources = getRepository().findAllByRoleIdAndStatusAndLastOrganSalePlanId(roleId, Status.ACTIVE.name()
                        , organPlan.get().getSalePlan().getId());
            } else {
                roleResources = getRepository().findAllByRole_IdAndStatus(roleId, Status.ACTIVE.name());
            }
        } else {
            roleResources = getRepository().findAllByRole_IdAndStatus(roleId, Status.ACTIVE.name());
        }
        return roleResources.stream().map(this::mapToDto).toList();
    }


    @Override
    public List<GrantedResourceDto> findAllByModelRoleId(Long roleId) {
        List<GrantedResource> grantedResources = new ArrayList<>();
        if (SecurityUtils.getCurrentOrgan().isPresent()) {
            Optional<OrganPlan> organPlan = organPlanService.getLastOrganSalePlan(SecurityUtils.getCurrentOrganIdOrElseThrow());
            if (organPlan.isPresent() && organPlan.get().getSalePlan() != null) {
                grantedResources = getRepository().findAllByModelRoleIdAndStatusAndLastOrganSalePlanId(roleId, Status.ACTIVE.name()
                        , organPlan.get().getSalePlan().getId());
            } else {
                grantedResources = getRepository().findAllByModelRole_IdAndStatus(roleId, Status.ACTIVE.name());
            }
        } else {
            grantedResources = getRepository().findAllByModelRole_IdAndStatus(roleId, Status.ACTIVE.name());
        }
        return grantedResources.stream().map(this::mapToDto).toList();
    }

    @Override
    public GrantedResourceDto mapToDto(GrantedResource grantedResource) {
        GrantedResourceDto grantedResourceDto = new GrantedResourceDto();
        grantedResourceDto.setId(grantedResource.getId());
        if (grantedResource.getModelRole() != null)
            grantedResourceDto.setModelRoleId(grantedResource.getModelRole().getId());
        if (grantedResource.getRole() != null)
            grantedResourceDto.setRoleId(grantedResource.getRole().getId());
//        Optional<OrganPlan> organPlan=organPlanService.getLastOrganSalePlan();
//        if(organPlan.isPresent()){
//
//        }
        grantedResourceDto.setResourceItemDto(resourceItemService.mapToDto(grantedResource.getResourceItem()));
        return grantedResourceDto;
    }

    @Override
    public List<GrantedResource> findAllGrantedResourceByResourceId(Long resourceId) {
        return this.getRepository().findAllByResourceItem_IdAndStatus(resourceId, Status.ACTIVE.name());
    }


}
