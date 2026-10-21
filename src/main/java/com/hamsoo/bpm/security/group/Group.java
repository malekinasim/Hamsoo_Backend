package com.hamsoo.bpm.security.group;


import com.hamsoo.bpm.base.BaseEntity;
import com.hamsoo.bpm.security.groupauthority.GroupAuthority;
import com.hamsoo.bpm.security.groupmember.GroupMember;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="GROUPS")
@Data
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="GROUP_ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="GROUP_NAME" , nullable = false, length = 50)
    private String groupName ;

    @OneToMany( mappedBy = "group",fetch = FetchType.LAZY)
    private List<GroupMember> groupMembers;


    @OneToMany( mappedBy = "group",fetch = FetchType.LAZY)
    private List<GroupAuthority> groupAuthorities ;


}
