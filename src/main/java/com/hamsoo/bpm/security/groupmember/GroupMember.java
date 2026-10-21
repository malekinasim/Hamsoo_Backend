package com.hamsoo.bpm.security.groupmember;


import com.hamsoo.bpm.security.group.Group;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="GROUP_MEMBERS")
@Data
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="GROUP_MEMBER_ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="USERNAME" , nullable = false, length = 50)
    String username;

    @ManyToOne( targetEntity = Group.class , fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Group group;

}
