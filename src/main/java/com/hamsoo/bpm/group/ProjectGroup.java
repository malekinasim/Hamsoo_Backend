package com.hamsoo.bpm.group;

import com.hamsoo.bpm.access.Access;
import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.member.Member;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name="PROJECT_GROUPS")
@Data
public class ProjectGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="GROUP_NAME" , nullable = false,length = 250)
    private String groupName;


    @OneToMany(mappedBy = "projectGroup",targetEntity = Member.class  , fetch = FetchType.LAZY)
    private List<Member> members;

    @OneToMany(mappedBy = "projectGroup",targetEntity = Access.class  , fetch = FetchType.LAZY)
    private List<Access> accceses;


}
