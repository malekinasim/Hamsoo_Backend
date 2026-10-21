package com.hamsoo.bpm.model.dto.modelapprover;

import com.hamsoo.bpm.model.dto.approverlog.ApproverLogDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public class ModelApproverDto {

    private Long id;

    @NotBlank(message = "error.modelApprover.userIsNotValid")
    private Long userId;

    @NotBlank(message = "error.modelApprover.modelVersionIsNotValid")
    private Long modelVersionId;

    @NotBlank(message = "error.modelApprover.orderIsNotValid")
    private Integer order;

    @NotBlank(message = "error.modelApprover.approveGroupIsNotValid")
    private String approveGroupCode;
    private String approveGroupCodeTitle;

    @NotBlank(message = "error.modelApprover.levelIsNotValid")
    private String level;
    private String levelTitle;


    //use just for search models that send for approve
    private String modelName;

    private List<String> modelTypeCode;

    private List<String> approveLevel;


    private Optional<ApproverLogDto> lastApproverLog;

    private String responseDescription;
    private String refrenceDescription;

    private UserDto userDto;

    private ModelVersionDto modelVersionDto;

    private Boolean deleteable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }


    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getApproveGroupCode() {
        return approveGroupCode;
    }

    public void setApproveGroupCode(String approveGroupCode) {
        this.approveGroupCode = approveGroupCode;
    }

    public String getApproveGroupCodeTitle() {
        return approveGroupCodeTitle;
    }

    public void setApproveGroupCodeTitle(String approveGroupCodeTitle) {
        this.approveGroupCodeTitle = approveGroupCodeTitle;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public ModelVersionDto getModelVersionDto() {
        return modelVersionDto;
    }

    public void setModelVersionDto(ModelVersionDto modelVersionDto) {
        this.modelVersionDto = modelVersionDto;
    }

    public Boolean getDeleteable() {
        return deleteable;
    }

    public void setDeleteable(Boolean deleteable) {
        this.deleteable = deleteable;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public Optional<ApproverLogDto> getLastApproverLog() {
        return lastApproverLog;
    }

    public void setLastApproverLog(Optional<ApproverLogDto> lastApproverLog) {
        this.lastApproverLog = lastApproverLog;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getRefrenceDescription() {
        return refrenceDescription;
    }

    public void setRefrenceDescription(String refrenceDescription) {
        this.refrenceDescription = refrenceDescription;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode(List<String> modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }

    public List<String> getApproveLevel() {
        return approveLevel;
    }

    public void setApproveLevel(List<String> approveLevel) {
        this.approveLevel = approveLevel;
    }
}
