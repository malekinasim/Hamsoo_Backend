package com.hamsoo.bpm.security.groupauthority;


import com.hamsoo.bpm.security.group.Group;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="GROUP_AUTHORITIES")
@Data
public class GroupAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="GROUP_AUTHORITY_ID" , nullable = false,precision = 0)
    private long id;

    @Basic
    @Column(name="AUTHORITY" , nullable = false, length = 50)
    private String authority ;

    @ManyToOne( targetEntity = Group.class , fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Group group ;

}
