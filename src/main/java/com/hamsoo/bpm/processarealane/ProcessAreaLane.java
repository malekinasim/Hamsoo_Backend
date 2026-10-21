package com.hamsoo.bpm.processarealane;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.enterprisemaplane.EnterpiseMapLane;
import com.hamsoo.bpm.processarea.ProcessArea;
import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name="PROCESS_AREA_LANES")
@Data
public class ProcessAreaLane extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @ManyToOne
    @JoinColumn( name="PROCESS_AREA_ID",referencedColumnName = "ID",nullable = false)
    private ProcessArea processArea;

    @ManyToOne
    @JoinColumn( name="ENTERPRISE_MAP_LANE_ID",referencedColumnName = "ID",nullable = false)
    private EnterpiseMapLane enterpiseMapLane;
}
