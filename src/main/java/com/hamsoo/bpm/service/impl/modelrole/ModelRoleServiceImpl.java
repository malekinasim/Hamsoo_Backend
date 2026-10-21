package com.hamsoo.bpm.service.impl.modelrole;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.modelrole.ModelRoleRepository;
import com.hamsoo.bpm.service.api.grantedresource.GrantedResourceService;
import com.hamsoo.bpm.service.api.modelrole.ModelRoleService;
import com.hamsoo.bpm.common.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service("modelRoleService")
public class ModelRoleServiceImpl implements ModelRoleService {


    private final ModelRoleRepository modelRoleRepository;
    private final GrantedResourceService grantedResourceService;

    public ModelRoleServiceImpl(ModelRoleRepository modelRoleRepository, GrantedResourceService grantedResourceService) {
        this.modelRoleRepository = modelRoleRepository;
        this.grantedResourceService = grantedResourceService;
    }

    @Override
    public ModelRoleRepository getRepository() {
        return modelRoleRepository;
    }

    @Override
    public ModelRole findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.modelRole.idIsNotValid");
        }
        return getRepository().findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new BusinessException("error.modelRole.idIsNotValid"));
    }

    @Override
    public ModelRole findByCodeOrElseThrow(String code) {
        if (code == null) {
            throw new BusinessException("error.modelRole.codeIsNotValid");
        }
        return getRepository().findByCodeAndStatus(code, Status.ACTIVE.name())
                .orElseThrow(() -> new BusinessException("error.modelRole.codeIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public ModelRoleDto mapToDto(ModelRole modelRole) {
        Assert.notNull(modelRole, "modelRole is null");
        modelRole = findByIdOrElseThrow(modelRole.getId());
        ModelRoleDto modelRoleDto = new ModelRoleDto();
        modelRoleDto.setId(modelRole.getId());
        modelRoleDto.setCode(modelRole.getCode());
        modelRoleDto.setName(modelRole.getName());
        List<GrantedResourceDto> grantedResourceDtos= grantedResourceService.findAllByModelRoleId(modelRole.getId());
        modelRoleDto.setGrantedResourceDtos(grantedResourceDtos);
        return modelRoleDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelRoleDto> list() {
        List<ModelRole> modelRoleList = getRepository().getByStatus(Status.ACTIVE.name(), PageUtils.getInstance());
        return CollectionUtils.isEmpty(modelRoleList) ? Collections.emptyList() : modelRoleList.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelRoleDto> findAllUserModelRoles() {
        List<ModelRole> modelRoleList = getRepository().findAllUserModelRoles(SecurityUtils.getUserIdOfCurrentUserOrElseThrow(),
                SecurityUtils.getCurrentOrganIdOrElseThrow(),Status.ACTIVE.name());
        return  modelRoleList.stream().map(this::mapToDto).toList();
    }


}
