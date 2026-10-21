package com.hamsoo.bpm.model.dto.modelmemberrole;

import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.dto.modelrole.ModelRoleDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class ModelMemberRoleDto {

    private Long id;
    @NotNull(message = "error.modelMemberRole.modelRoleIdIsNotValid")
    private Long modelRoleId;
    @NotNull(message = "error.modelMember.modelVersionIdIsNotValid")
    private Long modelVersionId;
    @NotNull(message = "error.modelMember.memberIdIsNotValid")
    private Long memberId;

    @NotBlank(message = "error.modelMember.memberTypeIsNotValid")
    private String memberType;

    //return property
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private ModelRoleDto modelRoleDto;
    private ModelMemberDto modelMemberDto;

    //return
    private List<String> ModelRoleCodeList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public ModelMemberDto getModelMemberDto() {
        return modelMemberDto;
    }

    public void setModelMemberDto(ModelMemberDto modelMemberDto) {
        this.modelMemberDto = modelMemberDto;
    }

    public Long getModelRoleId() {
        return modelRoleId;
    }

    public void setModelRoleId(Long modelRoleId) {
        this.modelRoleId = modelRoleId;
    }

    public ModelRoleDto getModelRoleDto() {
        return modelRoleDto;
    }

    public void setModelRoleDto(ModelRoleDto modelRoleDto) {
        this.modelRoleDto = modelRoleDto;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public List<String> getModelRoleCodeList() {
        return ModelRoleCodeList;
    }

    public void setModelRoleCodeList(List<String> modelRoleCodeList) {
        ModelRoleCodeList = modelRoleCodeList;
    }
}
