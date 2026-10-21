package com.hamsoo.bpm.model.dto.iocatalog;

import com.hamsoo.bpm.model.dto.iotype.IoTypeDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class IoCatalogDto {

    private Long id;
    @NotBlank(message = "error.ioCatalog.nameIsNotValid")
    private String name;
    @NotNull(message = "error.ioCatalog.ioTypeIdIsNotValid")
    private Long ioTypeId;
    private Long organId;

    //return property
    private IoTypeDto ioTypeDto;

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

    public Long getIoTypeId() {
        return ioTypeId;
    }

    public void setIoTypeId(Long ioTypeId) {
        this.ioTypeId = ioTypeId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public IoTypeDto getIoTypeDto() {
        return ioTypeDto;
    }

    public void setIoTypeDto(IoTypeDto ioTypeDto) {
        this.ioTypeDto = ioTypeDto;
    }
}
