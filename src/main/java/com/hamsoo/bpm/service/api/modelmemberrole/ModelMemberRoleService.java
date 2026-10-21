package com.hamsoo.bpm.service.api.modelmemberrole;


import com.hamsoo.bpm.model.dto.modelmember.ModelMemberUserDtoImpl;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.repository.jpa.modelmemberrole.ModelMemberRoleRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ModelMemberRoleService extends BaseService<ModelMemberRole, Long, ModelMemberRoleRepository> {

    ModelMemberRole findByIdOrElseThrow(Long id);

    ModelMemberRole passive(Long id);

    List<ModelMemberRole> findByModelMemberId(Long id);

    SearchDto list(ModelMemberRoleDto modelMemberRoleDto, int page, int size, String orderBy, Sort.Direction direction);

    ModelMemberRoleDto mapToDto(ModelMemberRole modelMemberRole);

    ModelMemberRoleDto mapToDtoByOnlineOfflineMode(ModelMemberRole modelMemberRole);

    List<ModelMemberRoleDto> mapToDto(List<ModelMemberRole> modelMemberRoles);

    ModelMemberRole create(ModelMemberRoleDto modelMemberRoleDto);

    ModelMemberRole edit(ModelMemberRoleDto modelMemberRoleDto);

    List<ModelMemberRoleDto> curUserModelRoleList(Long modelVersionId);


    List<ModelMemberRole> findByModelVersionIdAndUserIdAndStatus(Long modelVersionId, Long userId, String status);


    List<ModelMemberRole> findByModelVersionIdAndMemberIdAndMemberTypeAndRoleIdAndStatus(Long modelVersionId, Long memberId,String memberType, Long modelRoleId, String status);

    List<ModelMemberRole> findByUserIdAndStatusAndOrganId(Long id, String status, Long organId);

    ModelMemberRole changeOwner(ModelMemberRoleDto modelMemberRoleDto);

    List<ModelMemberUserDtoImpl> userList(Long modelVersionId);
}
