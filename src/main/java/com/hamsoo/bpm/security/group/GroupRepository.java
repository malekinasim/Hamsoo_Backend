package com.hamsoo.bpm.security.group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Long>, PagingAndSortingRepository<Group,Long> {

    @EntityGraph( attributePaths ={"groupAuthorities"} )
    @Query( value = "select  grp from ProjectGroup grp inner join GroupMember  grpmem " +
            " on grp=grpmem.group where grpmem.username=:username")
    List<Group> findAllGroupofUsers(String username);
}
