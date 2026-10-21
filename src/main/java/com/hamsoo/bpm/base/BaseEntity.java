package com.hamsoo.bpm.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {


    @JsonIgnore
    @Column(name="CREATE_DATE" )
    private Date createDate=new Date();

    @JsonIgnore
    @Column(name="UPDATE_DATE" )
    private Date updateDate=new Date();

    @JsonIgnore
    @Column(name="CREATE_BY" )
    private Date createBy;

    @JsonIgnore
    @Column(name="UPDATE_BY" )
    private Date updateBy;

    public BaseEntity() {
    }
}
