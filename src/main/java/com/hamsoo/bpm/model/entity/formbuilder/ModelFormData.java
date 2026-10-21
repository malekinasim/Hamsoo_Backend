package com.hamsoo.bpm.model.entity.formbuilder;

import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "bpm_model_form_data", schema = "bpm", catalog = "BPM_DB")
public class ModelFormData {

    private Long id;
    private FormSchemaCatalog formSchemaCatalog;
    private ModelVersion modelVersion;
    private String formData;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_form_data_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_schema_catalog_id")
    public FormSchemaCatalog getFormSchema() {
        return formSchemaCatalog;
    }

    public void setFormSchema(FormSchemaCatalog formSchemaCatalog) {
        this.formSchemaCatalog = formSchemaCatalog;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Column(name = "form_data")
    @Type(type = "json")
    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }
}
