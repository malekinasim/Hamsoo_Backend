package com.hamsoo.bpm.member;

import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.group.ProjectGroup;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="MEMBERS")
@Data
public class Member extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="USER_NAME" , nullable = false,length=250)
    private long userName;


    @ManyToOne
    @JoinColumn( name="GROUP_ID",referencedColumnName = "ID",nullable = false,unique = true)
    private ProjectGroup projectGroup;



}
