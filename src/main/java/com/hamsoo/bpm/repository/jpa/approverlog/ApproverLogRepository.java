package com.hamsoo.bpm.repository.jpa.approverlog;


import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//@Repository
@Transactional(rollbackFor = Exception.class)
public interface ApproverLogRepository extends HamsooRepository<ApproverLog, Long> {
    @Query(value = "select  log from ApproverLog log where log.modelApprover.id=:modelApproverId and" +
            " log.id= (select max(log1.id) from ApproverLog log1 where log1.modelApprover.id=:modelApproverId)  ")
    Optional<ApproverLog> findLastApproverLogOfModelAprover(@Param("modelApproverId") Long modelApproverId);


    @Query(value = "select  log from ApproverLog log " +
            " inner join log.modelApprover map " +
            " inner join map.modelMemberRole mmr" +
            " inner join mmr.modelMember mm " +
            " inner join mm.modelVersion mv" +
            " where  mv.id=:modelVersionId and" +
            " mm.status='ACTIVE' and mmr.status='ACTIVE' and map.status='ACTIVE'",
     countQuery = "select  count(log.id) from ApproverLog log " +
             " inner join log.modelApprover map " +
             " inner join map.modelMemberRole mmr" +
             " inner join mmr.modelMember mm " +
             " inner join mm.modelVersion mv" +
             " where  mv.id=:modelVersionId and" +
             " mm.status='ACTIVE' and mmr.status='ACTIVE' and map.status='ACTIVE'")
    Page<ApproverLog> findAllByModelVersionId(long modelVersionId, Pageable pageable);
}