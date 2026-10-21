package com.hamsoo.bpm.service.api.resourcecatalog;

import com.hamsoo.bpm.model.dto.resourcecatalog.ResourceCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog;
import com.hamsoo.bpm.repository.jpa.resourcecatalog.ResourceCatalogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ResourceCatalogService extends BaseService<ResourceCatalog, Long, ResourceCatalogRepository> {

    ResourceCatalog findByIdOrElseThrow(Long id);

    ResourceCatalogDto mapToDto(ResourceCatalog resourceCatalog);

    ResourceCatalog create(ResourceCatalogDto resourceCatalogDto);

    ResourceCatalog edit(ResourceCatalogDto resourceCatalogDto);

    void passive(Long id);

    SearchDto list(ResourceCatalogDto resourceCatalogDto, int page, int size, String orderBy, Sort.Direction direction);

    List<ResourceCatalogDto> getlistByResourceTypeId(Long id);
}