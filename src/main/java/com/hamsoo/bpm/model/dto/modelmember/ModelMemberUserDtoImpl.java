package com.hamsoo.bpm.model.dto.modelmember;

public class ModelMemberUserDtoImpl implements ModelMemberUserDto {
    private String name;
    private String family;
    private Long userId;
    private String nationalCode;
    private Long imageId;
    private String roles;
    private String roleNames;
    private String members;
    private boolean online;

    public ModelMemberUserDtoImpl(String name, String family,
                                  Long userId, String nationalCode,
                                  Long imageId, String roles, String roleNames,
                                  String members, boolean online) {
        this.name = name;
        this.family = family;
        this.userId = userId;
        this.nationalCode = nationalCode;
        this.imageId = imageId;
        this.roles = roles;
        this.roleNames = roleNames;
        this.members = members;
        this.online = online;
    }


    @Override
    public String getFamily() {
        return family;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public String getNationalCode() {
        return nationalCode;
    }

    @Override
    public Long getImageId() {
        return imageId;
    }

    @Override
    public String getRoles() {
        return roles;
    }

    @Override
    public String getRoleNames() {
        return roleNames;
    }

    @Override
    public String getMembers() {
        return members;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
