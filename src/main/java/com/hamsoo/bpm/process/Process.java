package com.hamsoo.bpm.process;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.ProcessVersion.ProcessVersion;
import com.hamsoo.bpm.proceessareaprocess.ProcessAreaProcess;
import com.hamsoo.bpm.project.Project;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="PROCESSES")
@Data
public class Process extends BaseEntity {

 @Id
 @GeneratedValue (strategy = GenerationType.IDENTITY)
 @Column(name="ID" , nullable = false,precision = 0)
  private long id;

  @Basic
  @Column(name="PROCESS_NAME" ,length = 250)
  private String  name;
  @Basic
  @Column(name="PROCESS_CODE" ,length = 250)
  private String  code;
  @Basic
  @Column(name="PROCESS_DESC" , length = 2000,nullable = true)
  private String  description;
  @Basic
  @Column(name="PROCESS_MISSION",length = 2000,nullable = true)
  private String mission;
  @Basic
  @Column(name="PROCESS_SCOPE" ,length = 2000,nullable = true)
  private String scope;


    @ManyToOne
    @JoinColumn( name="PROJECT_ID",referencedColumnName = "ID",nullable = false)
    private Project project;


 @OneToMany(mappedBy = "process",targetEntity = ProcessVersion.class, fetch = FetchType.LAZY)
 private List<ProcessVersion> processVersions;

 @OneToMany(mappedBy = "process",targetEntity = ProcessAreaProcess.class, fetch = FetchType.LAZY)
 private List<ProcessAreaProcess> processAreaProcess;

}
