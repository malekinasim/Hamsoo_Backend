package com.hamsoo.bpm.service.api.modelrole;

import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.repository.jpa.modelrole.ModelRoleRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ModelRoleService extends BaseService<ModelRole, Long, ModelRoleRepository> {
    ModelRole findByIdOrElseThrow(Long id);

    ModelRole findByCodeOrElseThrow(String code);

    ModelRoleDto mapToDto(ModelRole modelRole);

    List<ModelRoleDto> list();

    @Transactional(readOnly = true)
    List<ModelRoleDto> findAllUserModelRoles();
}
