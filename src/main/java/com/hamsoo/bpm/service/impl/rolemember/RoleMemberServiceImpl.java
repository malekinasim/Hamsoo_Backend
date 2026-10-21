package com.hamsoo.bpm.service.impl.rolemember;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.rolemember.RoleMemberRepository;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleMemberServiceImpl implements RoleMemberService {
    private RoleMemberRepository roleMemberRepository;
    private RoleService roleService;
    private UserService userService;
    private OrganService organService;

    @Override
    public RoleMemberRepository getRepository() {
        return roleMemberRepository;
    }

    public RoleMemberServiceImpl(RoleMemberRepository roleMemberRepository, RoleService roleService, @Lazy UserService userService, @Lazy OrganService organService) {
        this.roleMemberRepository = roleMemberRepository;
        this.roleService = roleService;
        this.userService = userService;
        this.organService = organService;
    }

    @Override
    public List<RoleMember> getDefaultRoleForPrivateWorkspace(Organ organ, User user, Account account) {
        List<RoleMember> roleMembers = new ArrayList<>();
        //add admin Role to user for His Workspace organ
        Role adminRole = roleService.getAdminRole();
        RoleMember adminRoleMember = new RoleMember();
        adminRoleMember.setOrgan(organ);
        adminRoleMember.setUser(user);
        adminRoleMember.setRole(adminRole);
        adminRoleMember.setCreatedBy(user);
        adminRoleMember.setModifiedBy(user);
        roleMembers.add(adminRoleMember);

        //add collector Role to user for His Workspace organ
        Role collectorRole = roleService.getCollectorRole();
        RoleMember cllectorRoleMember = new RoleMember();
        cllectorRoleMember.setOrgan(organ);
        cllectorRoleMember.setUser(user);
        cllectorRoleMember.setRole(collectorRole);
        cllectorRoleMember.setCreatedBy(user);
        cllectorRoleMember.setModifiedBy(user);
        roleMembers.add(cllectorRoleMember);

        //add uset Role to user for His Workspace organ
        Role userRole = roleService.getUserRole();
        RoleMember userRoleMember = new RoleMember();
        userRoleMember.setOrgan(organ);
        userRoleMember.setUser(user);
        userRoleMember.setRole(collectorRole);
        userRoleMember.setCreatedBy(user);
        userRoleMember.setModifiedBy(user);
        roleMembers.add(userRoleMember);
        return saveAll(roleMembers);
    }

    @Override
    public RoleMember findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.rolemember.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.rolemember.idIsNotValid"));
    }

    @Override
    public void deleteRoleMember(Long id) {
        RoleMember roleMember = findByIdOrElseThrow(id);
        delete(roleMember);
    }

    @Override
    public List<RoleMember> findAllByUser(Long userId) {
        try {
            return roleMemberRepository.findAllByUser(userId);
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    @Override
    public List<RoleMember> findAllByUserAndOrgan(Long userId, Long organId) {
        try {
            return roleMemberRepository.findAllByUserAndOrgan(userId, organId, Role.Role_Type.ORGAN_BASED.name());
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    @Override
    public List<RoleMember> findAllAppBasedRoleByUser(Long userId) {
        try {
            return roleMemberRepository.findAllByUserAndRoleType(userId, Role.Role_Type.APPLICATION_BASED.name());
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    @Override
    public List<RoleMember> findAllOrganBasedRoleByUser(Long userId) {
        try {
            return roleMemberRepository.findAllByUserAndRoleType(userId, Role.Role_Type.ORGAN_BASED.name());
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    @Override
    public List<RoleMember> findAllByOrganId(Long organId) {

        return roleMemberRepository.findAllByOrgan(organId);

    }

    @Override
    public RoleMemberDto mapToDto(RoleMember roleMember) {
        RoleMemberDto roleMemberDto = new RoleMemberDto();
        roleMemberDto.setId(roleMember.getId());
        roleMemberDto.setRoleDto(roleService.mapToDto(roleMember.getRole()));
        roleMemberDto.setRoleId(roleMember.getRole().getId());
        roleMemberDto.setUserDto(userService.mapToDto(roleMember.getUser()));
        roleMemberDto.setUserId(roleMember.getUser().getId());
        if (roleMember.getOrgan() != null)
            roleMemberDto.setOrganId(roleMember.getOrgan().getId());

        return roleMemberDto;
    }

    @Override
    public RoleMember create(RoleMemberDto roleMemberDto) {
        RoleMember roleMember = mapToEntity(roleMemberDto);
        return save(roleMember);
    }

    @Override
    public List<RoleMember> createAll(List<RoleMemberDto> roleMemberDtos) {
        List<RoleMember> roleMembers = roleMemberDtos.stream().map(this::mapToEntity).toList();
        return saveAll(roleMembers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passiveAll(List<RoleMember> roleMembers) {
        roleMembers.stream().map(RoleMember::getId).forEach(
                this::passive
        );
    }

    @Override
    public List<RoleMember> findAllByOrganIdAndRoleName(Long id, String code) {
        return roleMemberRepository.findAllByOrganAndRoleName(id, code);
    }

    public void passive(Long id) {
        RoleMember roleMember = findByIdOrElseThrow(id);
        roleMember.setStatus(Status.PASSIVE.name());
        this.save(roleMember);
    }


    private RoleMember mapToEntity(RoleMemberDto roleMemberDto) {
        Assert.notNull(roleMemberDto, "userOrganDto is null");
        Assert.notNull(roleMemberDto.getOrganId(), "organId id is null");
        Assert.notNull(roleMemberDto.getUserId(), "userId id is null");
        RoleMember roleMember = new RoleMember();
        roleMember.setUser(userService.findByIdOrElseThrow(roleMemberDto.getUserId()));

        roleMember.setRole(roleService.findByIdOrElseThrow(roleMemberDto.getRoleId()));

        if (roleMemberDto.getOrganId() != null) {
            Organ organ = organService.findByIdOrElseThrow(roleMemberDto.getOrganId());
            roleMember.setOrgan(organ);
        }
        roleMember.setStatus(Status.ACTIVE.name());

        return roleMember;
    }
}
