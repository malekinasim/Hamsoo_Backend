package com.hamsoo.bpm.model.dto.job;

import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class JobDto {

    private Long id;
    @NotBlank(message = "error.job.nameIsNotValid")
    @Size(max = 500, message = "error.job.nameSizeNotValid")
    private String name;
    @NotBlank(message = "error.job.codeIsNotValid")
    @Size(max = 10, message = "error.job.codeSizeNotValid")
    private String code;
    private String description;
    @NotNull(message = "error.job.chartIsNotValid")
    private Long chartId;
    private List<UserDto> userDtoList;
    //search
    private String searchWord;


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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
