package com.hamsoo.bpm.enterprisemaplane;


import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.enterprisemap.EnterpriseMap;
import com.hamsoo.bpm.processarealane.ProcessAreaLane;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="ENTERPRISEMAPLANES")
@Data
public class EnterpiseMapLane extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="NAME" , nullable = false,length = 250)
    private String name;

    @Basic
    @Column(name="DESCRIPTION" , nullable = false,length = 250)
    private String description;

   @ManyToOne
   @JoinColumn( name="ENTERPRISE_MAP_ID",referencedColumnName = "ID",nullable = false,unique = true)
    private EnterpriseMap enterpiseMap;


    @OneToMany(mappedBy = "enterpiseMapLane",targetEntity = ProcessAreaLane.class, fetch = FetchType.LAZY)
    private List<ProcessAreaLane> processAreaLanes;
}
