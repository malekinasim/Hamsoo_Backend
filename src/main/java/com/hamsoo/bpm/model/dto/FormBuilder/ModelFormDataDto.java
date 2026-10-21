package com.hamsoo.bpm.model.dto.FormBuilder;

import com.hamsoo.bpm.model.entity.formbuilder.FormSchemaCatalog;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

import javax.validation.constraints.NotBlank;

public class ModelFormDataDto {

    private Long id;
    @NotBlank(message = "error.formschema.nameIsNotValid")
    private FormSchemaCatalog formSchemaCatalog;
    @NotBlank(message = "error.formschema.nameIsNotValid")
    private ModelVersion modelVersion;
    @NotBlank(message = "error.formschema.nameIsNotValid")
    private String formData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormSchemaCatalog getFormSchema() {
        return formSchemaCatalog;
    }

    public void setFormSchema(FormSchemaCatalog formSchemaCatalog) {
        this.formSchemaCatalog = formSchemaCatalog;
    }

    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }


}
