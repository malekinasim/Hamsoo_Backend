package com.hamsoo.bpm.service.impl.resourceitem;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.ErrorDetails;
import com.hamsoo.bpm.common.utils.DateUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.resourceitem.ResourceItemDto;
import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;
import com.hamsoo.bpm.model.entity.grantedresource.GrantedResource;
import com.hamsoo.bpm.model.entity.organplan.OrganPlan;
import com.hamsoo.bpm.model.entity.resourceitem.ResourceItem;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.service.api.feature.FeatureService;
import com.hamsoo.bpm.service.api.grantedresource.GrantedResourceService;
import com.hamsoo.bpm.service.api.organplan.OrganPlanService;
import com.hamsoo.bpm.service.api.resourceitem.ResourceItemService;
import com.hamsoo.bpm.service.api.saleplanfeature.SalePlanFeatureService;
import com.hamsoo.bpm.repository.jpa.resourceitem.ResourceItemRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

@Service("resourceItemService")
public class ResourceItemServiceImpl implements ResourceItemService {
    private ResourceItemRepository resourceItemRepository;
    private FeatureService featureService;
    private OrganPlanService organPlanService;
    private SalePlanFeatureService salePlanFeatureService;
    private GrantedResourceService grantedResourceService;

    public ResourceItemServiceImpl(ResourceItemRepository resourceItemRepository,
                                   FeatureService featureService, @Lazy OrganPlanService organPlanService, SalePlanFeatureService salePlanFeatureService, GrantedResourceService grantedResourceService) {
        this.resourceItemRepository = resourceItemRepository;
        this.featureService = featureService;
        this.organPlanService = organPlanService;
        this.salePlanFeatureService = salePlanFeatureService;
        this.grantedResourceService = grantedResourceService;
    }

    @Override
    public ResourceItemRepository getRepository() {
        return resourceItemRepository;
    }

    @Override
    public ResourceItemDto mapToDto(ResourceItem resourceItem) {
        ResourceItemDto resourceItemDto = new ResourceItemDto();
        resourceItemDto.setId(resourceItem.getId());
        resourceItemDto.setDescription(resourceItem.getDescription());
        resourceItemDto.setName(resourceItem.getName());
        resourceItemDto.setStatus(resourceItem.getStatus());
        if (resourceItem.getFeature() != null)
            resourceItemDto.setFeatureDto(featureService.mapToDto(resourceItem.getFeature()));
        return resourceItemDto;
    }

    private ResourceItemDto mapToDtoByErrorDetailAndGrantedRole(ResourceItem resourceItem) throws UnknownHostException {
        ResourceItemDto resourceItemDto = mapToDto(resourceItem);
        ErrorDetails errorDetails = getResourceItemsAccessDeniedMsg(Collections.singletonList(resourceItem.getName()));
        resourceItemDto.setErrorDetails(errorDetails);
        List<GrantedResource> grantedResources = grantedResourceService.findAllGrantedResourceByResourceId(resourceItem.getId());
        List<String> grantedRoles = new ArrayList<>();
        grantedRoles.addAll(grantedResources.stream().map(gr -> gr.getModelRole()).filter(Objects::nonNull).map(role -> role.getCode()).toList());
        grantedRoles.addAll(grantedResources.stream().map(gr -> gr.getRole()).filter(Objects::nonNull).map(role -> role.getName()).toList());
        resourceItemDto.setGrantedRoles(grantedRoles);
        return resourceItemDto;
    }

    @Override
    public Optional<ResourceItem> finsByName(String name) {
        return this.getRepository().findByNameAndStatus(name, Status.ACTIVE.name());
    }

    @Override
    public ErrorDetails getResourceItemsAccessDeniedMsg(List<String> resourceNames) throws UnknownHostException {
        List<String> errors = new ArrayList<>();

        if (SecurityUtils.getCurrentOrgan().isPresent()) {

            List<ResourceItem> resourceItems = resourceNames.stream().map(this::finsByName).filter(Optional::isPresent).map(Optional::get).toList();

            resourceItems.stream().forEach(
                    resourceItem -> {
                        if (resourceItem.getFeature() != null) {
                            Optional<OrganPlan> organPlan = organPlanService.getLastOrganSalePlan(SecurityUtils.getCurrentOrganIdOrElseThrow());
                            List<SalePlanFeatureDto> salePlanFeatureDtos = salePlanFeatureService.findAllFeatureOfSalePlan(organPlan.get().getSalePlan().getId());
                            if (salePlanFeatureDtos.stream().map(SalePlanFeatureDto::getFeatureDto).filter(Objects::nonNull).noneMatch(featureDto -> featureDto.getId().equals(
                                    resourceItem.getFeature().getId()
                            ))) {
                                errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.OrganPlanFeature.Disable", resourceItem.getFeature().getName()));
                            } else {
                                if (organPlan.get().getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.FREE) &&
                                        organPlan.get().getSalePlan().getSaleServiceType().equals(SalePlan.ServicesType.SELF_HOST) &&
                                        organPlan.get().getValidTo().isBefore(LocalDateTime.now())) {
                                    errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.OrganPlanExpired",
                                            DateUtils.getDateTimePersianInString(organPlan.get().getValidTo(), "YYYY/MM/DD", "en")));
                                }
                            }
                        }
                    }
            );
        }
        if (errors.isEmpty()) errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.DefaultMsg"));

        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), null,
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());

        return errorDetails;
    }

    @Override
    public List<ResourceItemDto> list() throws UnknownHostException {
        List<ResourceItem> resourceItemList = getRepository().findAllByStatus(Status.ACTIVE.name());
        List<ResourceItemDto> resourceItemDtos = new ArrayList<>();
        for (ResourceItem resourceItem : resourceItemList) {
            resourceItemDtos.add(mapToDtoByErrorDetailAndGrantedRole(resourceItem));
        }
        return resourceItemDtos;
    }

    @Override
    public ResourceItemDto getResourceItemByNamAndErrorDetails(String name) throws UnknownHostException {
        ResourceItem resourceItem = this.getRepository().findByNameAndStatus(name, Status.ACTIVE.name()).orElseThrow(
                () -> new BusinessException("error.resourceItem.notFound")
        );
        return mapToDto(resourceItem);
    }
}
