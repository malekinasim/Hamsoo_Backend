package com.hamsoo.bpm.service.api.rolemember;

import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.repository.jpa.rolemember.RoleMemberRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface RoleMemberService extends BaseService<RoleMember, Long, RoleMemberRepository> {

    List<RoleMember> getDefaultRoleForPrivateWorkspace(Organ organ, User user, Account account);

    RoleMember findByIdOrElseThrow(Long id);

    void deleteRoleMember(Long id);

    List<RoleMember> findAllByUser(Long userId);

    List<RoleMember> findAllByUserAndOrgan(Long user, Long organ);

    List<RoleMember> findAllAppBasedRoleByUser(Long userId);

    List<RoleMember> findAllOrganBasedRoleByUser(Long userId);

    List<RoleMember> findAllByOrganId(Long id);

    RoleMemberDto mapToDto(RoleMember roleMember);

    RoleMember create(RoleMemberDto roleMemberDto);

    List<RoleMember> createAll(List<RoleMemberDto> roleMemberDtos);

    void passiveAll(List<RoleMember> toList);

    List<RoleMember> findAllByOrganIdAndRoleName(Long id, String code);
}