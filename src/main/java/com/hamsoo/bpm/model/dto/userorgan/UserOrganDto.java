package com.hamsoo.bpm.model.dto.userorgan;

import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.user.UserDto;

public class UserOrganDto {

    private Long id;
    private Long organId;
    private Long userId;
    private Long personId;

    private UserDto userDto;

    private OrganDto organDto;

    private Long roleId;

    private RoleDto roleDto;

    private int userModelCount;

    public UserOrganDto() {
    }

    public int getUserModelCount() {
        return userModelCount;
    }

    public void setUserModelCount(int userModelCount) {
        this.userModelCount = userModelCount;
    }


    public RoleDto getRoleDto() {
        return roleDto;
    }

    public void setRoleDto(RoleDto roleDto) {
        this.roleDto = roleDto;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public OrganDto getOrganDto() {
        return organDto;
    }

    public void setOrganDto(OrganDto organDto) {
        this.organDto = organDto;
    }
}
