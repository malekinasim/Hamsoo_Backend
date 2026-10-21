package com.hamsoo.bpm.access;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.group.ProjectGroup;
import com.hamsoo.bpm.project.Project;
import lombok.Data;

import javax.persistence.*;
@Entity
@Table(name="ACCESSES")

@Data
public class Access extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;
    @Basic
    @Column(name="ACOUNT_TYPE" , nullable = false, length = 20)
    private String acountType;

    @Basic
    @Column(name="USER_NAME" , nullable = false, length = 20)
    private String userName;

    @Basic
    @Column(name="ACCESS_TYPE" , nullable = false, length = 20)
    private String accessType;


    @ManyToOne
    @JoinColumn( name="PROJECT_ID",referencedColumnName = "ID",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn( name="Group_ID",referencedColumnName = "ID",nullable = false)
    private ProjectGroup projectGroup;






}
