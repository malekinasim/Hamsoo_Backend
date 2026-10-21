package com.hamsoo.bpm.service.api.role;

import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.repository.jpa.role.RoleRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

public interface RoleService extends BaseService<Role, Long, RoleRepository> {
    Role findByIdOrElseThrow(Long id);

    Role getAdminRole();

    Role getCollectorRole();

    Role getUserRole();


    RoleDto mapToDto(Role role);

    RoleDto mapToDtoByResourceInfo(Role role, Long organId);

    SearchDto list(RoleDto roleDto, int i, int maxPageSize, String orderBy, Sort.Direction direction);
}