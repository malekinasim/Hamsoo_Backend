package com.hamsoo.bpm.service.api.resourcetype;

import com.hamsoo.bpm.model.dto.resourcetype.ResourceTypeDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;
import com.hamsoo.bpm.repository.jpa.resourcetype.ResourceTypeRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ResourceTypeService extends BaseService<ResourceType, Long, ResourceTypeRepository> {

    ResourceType findByIdOrElseThrow(Long id);

    ResourceTypeDto mapToDto(ResourceType resourceType);

    List<ResourceTypeDto> mapToDto(List<ResourceType> resourceTypeList);

    List<ResourceTypeDto> getList();

    ResourceType create(ResourceTypeDto resourceTypeDto);

    ResourceType edit(ResourceTypeDto resourceTypeDto);


    SearchDto list(ResourceTypeDto resourceTypeDto, int page, int size, String orderBy, Sort.Direction direction);

    void passive(Long id);
}