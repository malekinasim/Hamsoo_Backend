package com.hamsoo.bpm.model.dto.model;

import com.hamsoo.bpm.model.dto.BpmnReport.BpmnReportDto;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.organ.Organ;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class ModelDto {

    private Long id;
    @NotBlank(message = "error.model.nameIsNotValid")
    private String name;
    @NotBlank(message = "error.model.codeIsNotValid")
    private String code;
    @NotBlank(message = "error.model.modelTypeCodeIsNotValid")
    private String modelTypeCode;
    private String modelTypeTitle;
    private Long ownerUserId;// for users molel list in userManagement link
    private Long responsibleUnitId;

    //return property
    private LocalDateTime modifiedDate;
    private ModelVersionDto lastAsisVersion;
    private ModelVersionDto lastTobeVersion;
    private Long organId;
    private String organName;
    private LocalDateTime modelLastModificationDate;
    private LocalDateTime modelAssignDate;
    private String modelAssignStatusTitle;
    private String modelAssignStatusCode;
    private UnitDto responsibleUnit;
    private Boolean favorite;
    private UserFavoriteDto userFavoriteDto;

    private String AsisRole;

    private String TobeRole;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getTobeRole() {
        return TobeRole;
    }

    public String getAsisRole() {
        return AsisRole;
    }

    public void setAsisRole(String asisRole) {
        AsisRole = asisRole;
    }

    public void setTobeRole(String tobeRole) {
        TobeRole = tobeRole;
    }

    //search property
    private List<String> modelTypeCodeList;
    /**
     * see {@link ModelVersion#getStatus()}.
     */
    private List<String> statusCodeList;
    /**
     * see {@link ModelRole#getCode()}.
     */
    private List<String> modelRoleCodeList;

    public List<BpmnReportDto> getBpmnReportDtoList() {
        return bpmnReportDtoList;
    }

    public void setBpmnReportDtoList(List<BpmnReportDto> bpmnReportDtoList) {
        this.bpmnReportDtoList = bpmnReportDtoList;
    }

    private List<BpmnReportDto>  bpmnReportDtoList;
    public ModelDto() {
    }

    public ModelDto(Model model, ModelVersion modelVersion, ApproverLog approverLog, Organ organ, CodeCategory level) {
        Assert.notNull(model, "model is null");
        Assert.notNull(model.getId(), "model's id is null");
        this.setId(model.getId());
        this.setName(model.getName());
        this.setCode(model.getCode());
        this.setModelTypeCode(model.getModelType().getCode());
        this.setModelTypeTitle(model.getModelType().getFaName());
        this.setModifiedDate(model.getModifiedDate());
        this.setOrganId(organ.getId());
        this.setOrganName(organ.getName());
        this.setModelLastModificationDate(model.getModelLastModificationDate());
        this.setModelAssignDate(approverLog.getCreatedDate());
        this.setModelAssignStatusTitle(level.getFaName());
        this.setModelAssignStatusCode(level.getCode());
        ModelVersionDto modelVersionDto = new ModelVersionDto(modelVersion);
        if (modelVersion.getAsisTobe().getCode().equals(ModelVersion.AsisTobe.AS_IS.getCode())) {
            this.setLastAsisVersion(modelVersionDto);
        } else {
            this.setLastTobeVersion(modelVersionDto);
        }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode(String modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }

    public String getModelTypeTitle() {
        return modelTypeTitle;
    }

    public void setModelTypeTitle(String modelTypeTitle) {
        this.modelTypeTitle = modelTypeTitle;
    }

    public Long getResponsibleUnitId() {
        return responsibleUnitId;
    }

    public void setResponsibleUnitId(Long responsibleUnitId) {
        this.responsibleUnitId = responsibleUnitId;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public UserFavoriteDto getUserFavoriteDto() {
        return userFavoriteDto;
    }

    public void setUserFavoriteDto(UserFavoriteDto userFavoriteDto) {
        this.userFavoriteDto = userFavoriteDto;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ModelVersionDto getLastAsisVersion() {
        return lastAsisVersion;
    }

    public void setLastAsisVersion(ModelVersionDto lastAsisVersion) {
        this.lastAsisVersion = lastAsisVersion;
    }

    public ModelVersionDto getLastTobeVersion() {
        return lastTobeVersion;
    }

    public void setLastTobeVersion(ModelVersionDto lastTobeVersion) {
        this.lastTobeVersion = lastTobeVersion;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public LocalDateTime getModelLastModificationDate() {
        return modelLastModificationDate;
    }

    public void setModelLastModificationDate(LocalDateTime modelLastModificationDate) {
        this.modelLastModificationDate = modelLastModificationDate;
    }

    public UnitDto getResponsibleUnit() {
        return responsibleUnit;
    }

    public void setResponsibleUnit(UnitDto responsibleUnit) {
        this.responsibleUnit = responsibleUnit;
    }

    public List<String> getModelTypeCodeList() {
        return modelTypeCodeList;
    }

    public void setModelTypeCodeList(List<String> modelTypeCodeList) {
        this.modelTypeCodeList = modelTypeCodeList;
    }

    public List<String> getStatusCodeList() {
        return statusCodeList;
    }

    public void setStatusCodeList(List<String> statusCodeList) {
        this.statusCodeList = statusCodeList;
    }

    public List<String> getModelRoleCodeList() {
        return modelRoleCodeList;
    }

    public void setModelRoleCodeList(List<String> modelRoleCodeList) {
        this.modelRoleCodeList = modelRoleCodeList;
    }

    public LocalDateTime getModelAssignDate() {
        return modelAssignDate;
    }

    public void setModelAssignDate(LocalDateTime modelAssignDate) {
        this.modelAssignDate = modelAssignDate;
    }

    public String getModelAssignStatusTitle() {
        return modelAssignStatusTitle;
    }

    public void setModelAssignStatusTitle(String modelAssignStatusTitle) {
        this.modelAssignStatusTitle = modelAssignStatusTitle;
    }

    public String getModelAssignStatusCode() {
        return modelAssignStatusCode;
    }

    public void setModelAssignStatusCode(String modelAssignStatusCode) {
        this.modelAssignStatusCode = modelAssignStatusCode;
    }
}
