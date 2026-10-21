package com.hamsoo.bpm.processarea;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.proceessareaprocess.ProcessAreaProcess;
import com.hamsoo.bpm.processarealane.ProcessAreaLane;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="PROCESS_AREAS")
@Data
public class ProcessArea extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

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
    @Column(name="XML_MODEL" ,columnDefinition="xml", nullable = false,length = 20000)
    private String xmlModel;


    @Basic
    @Column(name="PROCESS_AREA_NAME" ,length = 250)
    private String  name;
    @Basic
    @Column(name="PROCESS_AREA_CODE" ,length = 250)
    private String  code;
    @Basic
    @Column(name="PROCESS_AREA_DESC" ,length = 2000,nullable = true)
    private String  description;

    @ManyToOne
    @JoinColumn( name="PARENT_ID",referencedColumnName = "ID",nullable = true)
    private ProcessArea parentProcessArea;


    @OneToMany(mappedBy = "parentProcessArea",targetEntity = ProcessArea.class, fetch = FetchType.LAZY)
    private List<ProcessArea> processAreas;


    @OneToMany(mappedBy = "processArea",targetEntity = ProcessAreaLane.class, fetch = FetchType.LAZY)
    private List<ProcessAreaLane> processAreaLanes;

    @OneToMany(mappedBy = "processArea",targetEntity = ProcessAreaProcess.class, fetch = FetchType.LAZY)
    private List<ProcessAreaProcess> processAreaProcess;



}
