package com.hamsoo.bpm.project;


import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.access.Access;
import com.hamsoo.bpm.process.Process;
import com.hamsoo.bpm.enterprisemap.EnterpriseMap;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="PROJECTS")
@Data
public class Project extends BaseEntity {

   @Id
   @GeneratedValue (strategy = GenerationType.IDENTITY)
   @Column(name="ID" , nullable = false,precision = 0)
    private long id;

   @Basic
   @Column(name="PROJECT_NAME" , nullable = false,length = 250)
   private String projectName;
   @Basic
   @Column(name="PROJECT_TYPE" , nullable = false,length = 5)
   private String projectType;
   @Basic
   @Column(name="PROJECT_CATEGORY" , nullable = true,length = 250)
   private String ProjectCategory;

   @OneToMany(mappedBy = "project",targetEntity = Access.class, fetch = FetchType.LAZY)
   private List<Access> accesses;


   @OneToMany(mappedBy = "project",targetEntity = EnterpriseMap.class, fetch = FetchType.LAZY)
   private List<EnterpriseMap> enterpriseMaps;



   @OneToMany(mappedBy = "project",targetEntity = Process.class, fetch = FetchType.LAZY)
   private List<Process> processes;

}
