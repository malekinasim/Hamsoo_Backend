package com.hamsoo.bpm.model.dto.modelversion;

import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.identity.IdentityDataSchema;
import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class ModelVersionDto {

    private Long id;
    private Integer versionNum;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String asisTobeCode;
    private String asisTobeTitle;
    private String statusCode;
    private String statusTitle;
    private Long modelId;
    private Boolean lastVersion;
    private Long xmlFileId;
    private Long imageFileId;

    private Long workflowFileId;

    private FilesDto xml;
    private FilesDto image;

    private FilesDto workFlowFile;

  //  private List<ResourceSchema> resourceSchemaList;
    private IdentityDataSchema identityDataSchema;
    private Long parentId;
    @NotBlank(message = "error.modelversion.milestoneNameIsNotValid", groups = {Milestone.class})
    @Size(max = 100, message = "error.modelversion.milestoneNameMaxLengthIsNotValid", groups = {Milestone.class})
    private String milestoneName;

    //return property
    private LocalDateTime modifiedDate;
    private List<ModelMemberRoleDto> modelMemberDtoList;
    private UserDto createdBy;
    private String modelNameWithAsisTobe;
    private String modelName;

    private ModelVersionDto parentReferenceModelVersion;

    private String validationType;//for validation Dashboard
    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    private Long noCategoryCount;
    private Long noInputCount;
    private Long noOutputCount;
    private Long noSIPOCValidCount;
    private Long noCustomerCount;
    private Long noSupplierCount;

    public Long getNoCategoryCount() {
        return noCategoryCount;
    }

    public void setNoCategoryCount(Long noCategoryCount) {
        this.noCategoryCount = noCategoryCount;
    }

    public Long getNoInputCount() {
        return noInputCount;
    }

    public void setNoInputCount(Long noInputCount) {
        this.noInputCount = noInputCount;
    }

    public Long getNoOutputCount() {
        return noOutputCount;
    }

    public void setNoOutputCount(Long noOutputCount) {
        this.noOutputCount = noOutputCount;
    }

    public Long getNoSIPOCValidCount() {
        return noSIPOCValidCount;
    }

    public void setNoSIPOCValidCount(Long noSIPOCValidCount) {
        this.noSIPOCValidCount = noSIPOCValidCount;
    }

    public Long getNoCustomerCount() {
        return noCustomerCount;
    }

    public void setNoCustomerCount(Long noCustomerCount) {
        this.noCustomerCount = noCustomerCount;
    }

    public Long getNoSupplierCount() {
        return noSupplierCount;
    }

    public void setNoSupplierCount(Long noSupplierCount) {
        this.noSupplierCount = noSupplierCount;
    }

    public Long getWorkflowFileId() {
        return workflowFileId;
    }

    public void setWorkflowFileId(Long workflowFileId) {
        this.workflowFileId = workflowFileId;
    }

    public ModelVersionDto() {
    }

    public ModelVersionDto(Integer id,Integer modelId,String name,String modelType ) {
        this.setId(Long.valueOf(id));
        this.setModelId(Long.valueOf(modelId));
        this.setModelName(name);
        this.setAsisTobeTitle(modelType);//ASIS-TOBE
    }
    public ModelVersionDto(ModelVersion modelVersion) {

        Assert.notNull(modelVersion, "modelVersion is null");
        this.setId(modelVersion.getId());
        this.setVersionNum(modelVersion.getVersionNum());
        this.setValidFrom(modelVersion.getValidFrom());
        this.setValidTo(modelVersion.getValidTo());
        this.setAsisTobeCode(modelVersion.getAsisTobe().getCode());
        this.setAsisTobeTitle(modelVersion.getAsisTobe().getFaName());
        this.setStatusCode(modelVersion.getStatus().getCode());
        this.setStatusTitle(modelVersion.getStatus().getFaName());
        this.setModelId(modelVersion.getModel().getId());
        this.setLastVersion(modelVersion.getLastVersion());
        if (modelVersion.getXml() != null) {
            this.setXmlFileId(modelVersion.getXml().getId());
        }
        if (modelVersion.getImage() != null) {
            this.setImageFileId(modelVersion.getImage().getId());
        }
        this.setModifiedDate(modelVersion.getModifiedDate());
        if (modelVersion.getParent() != null) {
            this.setParentId(modelVersion.getParent().getId());
        }
        this.setMilestoneName(modelVersion.getMilestoneName());
        this.setModelName(modelVersion.getModel().getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getAsisTobeCode() {
        return asisTobeCode;
    }

    public void setAsisTobeCode(String asisTobeCode) {
        this.asisTobeCode = asisTobeCode;
    }

    public String getAsisTobeTitle() {
        return asisTobeTitle;
    }

    public void setAsisTobeTitle(String asisTobeTitle) {
        this.asisTobeTitle = asisTobeTitle;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public Long getXmlFileId() {
        return xmlFileId;
    }

    public void setXmlFileId(Long xmlFileId) {
        this.xmlFileId = xmlFileId;
    }

    public Long getImageFileId() {
        return imageFileId;
    }

    public void setImageFileId(Long imageFileId) {
        this.imageFileId = imageFileId;
    }

    public FilesDto getXml() {
        return xml;
    }

    public void setXml(FilesDto xml) {
        this.xml = xml;
    }

    public FilesDto getImage() {
        return image;
    }

    public void setImage(FilesDto image) {
        this.image = image;
    }

//    public List<ResourceSchema> getResourceSchemaList() {
//        return resourceSchemaList;
//    }
//
//    public void setResourceSchemaList(List<ResourceSchema> resourceSchemaList) {
//        this.resourceSchemaList = resourceSchemaList;
//    }

    public IdentityDataSchema getIdentityDataSchema() {
        return identityDataSchema;
    }

    public void setIdentityDataSchema(IdentityDataSchema identityDataSchema) {
        this.identityDataSchema = identityDataSchema;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<ModelMemberRoleDto> getModelMemberDtoList() {
        return modelMemberDtoList;
    }

    public void setModelMemberDtoList(List<ModelMemberRoleDto> modelMemberDtoList) {
        this.modelMemberDtoList = modelMemberDtoList;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public String getModelNameWithAsisTobe() {
        return modelNameWithAsisTobe;
    }

    public void setModelNameWithAsisTobe(String modelNameWithAsisTobe) {
        this.modelNameWithAsisTobe = modelNameWithAsisTobe;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public FilesDto getWorkFlowFile() {
        return workFlowFile;
    }

    public void setWorkFlowFile(FilesDto workFlowFile) {
        this.workFlowFile = workFlowFile;
    }

    public interface Milestone {

    }

    public ModelVersionDto getParentReferenceModelVersion() {
        return parentReferenceModelVersion;
    }

    public void setParentReferenceModelVersion(ModelVersionDto parentRefrenceModelVersion) {
        this.parentReferenceModelVersion = parentRefrenceModelVersion;
    }
}
