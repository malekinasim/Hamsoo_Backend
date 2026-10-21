package com.hamsoo.bpm.model.dto.post;

import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class PostDto {
    private Long id;
    @NotBlank(message = "error.post.nameIsNotValid")
    @Size(max = 500, message = "error.post.nameSizeNotValid")
    private String name;
    private String description;
    @NotBlank(message = "error.post.codeIsNotValid")
    @Size(max = 10, message = "error.post.codeSizeNotValid")
    private String code;
    @NotNull(message = "error.unit.unitIdIsNotValid")
    private Long unitId;
    @NotNull(message = "error.job.jobIdIsNotValid")
    private Long jobId;
    @NotNull(message = "error.chart.chartIdIsNotValid")
    private Long chartId;
    //search
    private String searchWord;

    private String jobName;

    private List<UserDto> userDtoList;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public List<UserDto> getUserDtoList() {
        return userDtoList;
    }

    public void setUserDtoList(List<UserDto> userDtoList) {
        this.userDtoList = userDtoList;
    }
}
