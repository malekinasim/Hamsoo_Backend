package com.hamsoo.bpm.enterprisemap;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.enterprisemaplane.EnterpiseMapLane;
import com.hamsoo.bpm.project.Project;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="ENTERPRISEMAPS")
@Data
public class EnterpriseMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="ENTERPRISEMAP__NAME" ,length = 250)
    private String  name;
    @Basic
    @Column(name="ENTERPRISEMAP_CODE" ,length = 250)
    private String  code;
    @Basic
    @Column(name="ENTERPRISEMAP_DESC" ,length = 2000,nullable = true)
    private String  description;

    @Basic
    @Column(name="VERSION" , nullable = false,precision = 0)
    private Integer version=1;

    @Basic
    @Column(name="VALID_FROM" , nullable = false,precision = 0)
    private Date validFrom=new Date();

    @Basic
    @Column(name="VALID_TO" , nullable = false,precision = 0)
    private Date validTo;

    @Basic
    @Column(name="XML_MODEL",columnDefinition="xml" , nullable = false,length = 20000)
    private String xmlModel;


    @OneToMany(mappedBy = "enterpiseMap",targetEntity = EnterpiseMapLane.class, fetch = FetchType.LAZY)
    private List<EnterpiseMapLane> enterpiseMapLanes;

    @ManyToOne
    @JoinColumn( name="PROJECT_ID",referencedColumnName = "ID",nullable = false)
    private Project project;


}
