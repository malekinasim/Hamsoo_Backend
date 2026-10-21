package com.hamsoo.bpm.repository.jpa.userfavorite;

import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface UserFavoriteRepository extends HamsooRepository<UserFavorite, Long> {

    @Query(value = "select uf from UserFavorite uf inner join uf.user u " +
            "where uf.referenceType= :referenceType and uf.referenceId= :referenceId and u.id= :userId ")
    List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserId(@Param("referenceType") String referenceType,
                                                                  @Param("referenceId") Long referenceId, @Param("userId") Long userId);

    @Query(value = "select uf from UserFavorite uf inner join uf.user u " +
            "where uf.referenceType= :referenceType and uf.referenceId= :modelId and u.id in :userList ")
    List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserListId(String referenceType, Long modelId, List<Long> userList);
}
