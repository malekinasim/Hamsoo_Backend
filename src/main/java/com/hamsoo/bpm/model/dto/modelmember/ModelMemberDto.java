package com.hamsoo.bpm.model.dto.modelmember;

import com.hamsoo.bpm.model.dto.job.JobDto;
import com.hamsoo.bpm.model.dto.post.PostDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ModelMemberDto {

    private Long id;
    @NotNull(message = "error.modelMember.modelVersionIsNotValid")
    private Long modelVersionId;
    @NotNull(message = "error.modelMember.memberTypeIsNotValid")
    private String memberType;
    @NotNull(message = "error.modelMember.memberIdIsNotValid")
    private Long memberId;

    private Long modelMemberRoleId;

    //return property
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private UserDto userDto;
    private PostDto postDto;
    private JobDto jobDto;
    private UnitDto unitDto;

    private Boolean online;

    public ModelMemberDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Long getModelMemberRoleId() {
        return modelMemberRoleId;
    }

    public void setModelMemberRoleId(Long modelMemberRoleId) {
        this.modelMemberRoleId = modelMemberRoleId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public PostDto getPostDto() {
        return postDto;
    }

    public void setPostDto(PostDto postDto) {
        this.postDto = postDto;
    }

    public JobDto getJobDto() {
        return jobDto;
    }

    public void setJobDto(JobDto jobDto) {
        this.jobDto = jobDto;
    }

    public UnitDto getUnitDto() {
        return unitDto;
    }

    public void setUnitDto(UnitDto unitDto) {
        this.unitDto = unitDto;
    }
}


