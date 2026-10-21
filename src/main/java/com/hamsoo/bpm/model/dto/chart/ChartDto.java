package com.hamsoo.bpm.model.dto.chart;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChartDto {
    private Long id;
    @NotBlank(message = "error.chart.nameIsNotValid")
    @Size(max = 500, message = "error.chart.nameSizeNotValid")
    private String name;
    private String description;
    @NotBlank(message = "error.chart.codeIsNotValid")
    @Size(max = 10, message = "error.chart.codeSizeNotValid")
    private String code;

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
}
