package com.hamsoo.bpm.ProcessVersion;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.process.Process;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="PROCESSE_VERSIONS")
@Data
public class ProcessVersion extends BaseEntity {
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
    @Column(name="STATE" , nullable = false,length = 20000)
   private String State="DRAFT";
    @Basic
    @Column(name="STATUS" , nullable = false,length = 20000)
     private String status="TO_BE";

    @ManyToOne
    @JoinColumn( name="PROCESS_ID",referencedColumnName = "ID",nullable = false)
    private Process process;


}
