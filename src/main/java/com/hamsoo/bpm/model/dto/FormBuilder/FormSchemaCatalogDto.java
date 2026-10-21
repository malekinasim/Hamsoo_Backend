package com.hamsoo.bpm.model.dto.FormBuilder;


import javax.validation.constraints.NotBlank;

public class FormSchemaCatalogDto {

    private Long id;
    private String jsonContent;
    @NotBlank(message = "error.formschema.nameIsNotValid")
    private String name;

    private String status;
    private String runStatus;//code of category:Draft & Published

    private String RunStatusFaName;

    private String code;


    public String getRunStatusFaName() {
        return RunStatusFaName;
    }

    public void setRunStatusFaName(String runStatusFaName) {
        RunStatusFaName = runStatusFaName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunStatus() {
        return runStatus;
    }
    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
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


}
