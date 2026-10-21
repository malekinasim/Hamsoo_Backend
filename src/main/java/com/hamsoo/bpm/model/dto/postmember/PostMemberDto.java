package com.hamsoo.bpm.model.dto.postmember;

import com.hamsoo.bpm.model.entity.person.Person;

import javax.validation.constraints.NotNull;

public class PostMemberDto {
    private Long id;

    @NotNull(message = "error.postMember.postIdsNotValid")
    private Long postId;
    @NotNull(message = "error.postMember.personIdsNotValid")
    private Long personId;
    private Long jobId;
    private Long unitId;
    @NotNull(message = "error.postMember.chartIdsNotValid")
    private Long chartId;
    private String postName;
    private String postCode;
    private String personFullName;
    private String jobName;
    private String jobCode;
    private String unitName;
    private String unitCode;

    //searchWord
    private String searchWord;



    private Person personInfo;

    public Person getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(Person personInfo) {
        this.personInfo = personInfo;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPersonFullName() {
        return personFullName;
    }

    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }


}
