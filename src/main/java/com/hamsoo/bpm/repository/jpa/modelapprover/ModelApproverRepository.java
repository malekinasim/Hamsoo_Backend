package com.hamsoo.bpm.repository.jpa.modelapprover;

import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelApproverRepository extends HamsooRepository<ModelApprover, Long> {

    //TODO must be change after changed approver to set post job or unit member as approver
    @Query(value = "select mvp from " +
            "ModelApprover  mvp " +
            "inner join mvp.approveGroup apvg " +
            "inner join mvp.modelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            " where mv.id=:modelVersionId and" +
            " apvg.code=:groupCode and" +
            " apvg.category=:codeCategory and" +
            " u.id=:userId and " +
            " mvp.status=:status")
    Optional<ModelApprover> findByModelVersionIdAndApproveGroupCodeAndCodeCategoryAndUserIdAndStatus(
            Long modelVersionId, String groupCode, String codeCategory, Long userId, String status);

    @Query(value = "select mvp from " +
            "ModelApprover  mvp " +
            "inner join mvp.modelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            " where mv.id=:modelVersionId and" +
            " mvp.order >:order and " +
            " mvp.status='ACTIVE'")
    List<ModelApprover> findAllByModelVersionIdAndOrderGreaterThan(Long modelVersionId, int order);


    @Query(value = "select mvp from " +
            "ModelApprover  mvp " +
            "inner join mvp.modelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            " where mv.id=:modelVersionId and" +
            " (mvp.order BETWEEN :minOrder AND :maxOrder) and " +
            " mvp.status='ACTIVE'")
    List<ModelApprover> findAllByModelVersionIdAndOrderBetween(Long modelVersionId, int minOrder, int maxOrder);


    @Query(value = "select mvp from " +
            "ModelApprover  mvp " +
            "inner join mvp.modelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            " where mv.id=:modelVersionId and" +
            " mvp.order =:oreder  and " +
            " mvp.status='ACTIVE'")
    Optional<ModelApprover> findAllByModelModelVersionIdAndOrder(Long modelVersionId, int oreder);

    @Query(value = "select distinct modelApprover from ModelApprover modelApprover" +
            " inner join modelApprover.modelMemberRole mmr" +
            " inner join mmr.modelMember mm " +
            " inner join mm.modelVersion mv" +
            " where  mv.id= :modelVersionId" +
            " and modelApprover.status='ACTIVE'" +
            " and modelApprover.order=(" +
            " select min(modelapp.order) from ModelApprover modelapp" +
            " inner join modelapp.modelMemberRole mmr" +
            " inner join mmr.modelMember mm " +
            " inner join mm.modelVersion mv1" +
            " where modelapp.id not in (select log.modelApprover.id from ApproverLog  log " +
            "  where  log.status='ACTIVE') and modelapp.status='ACTIVE'  " +
            "   and mv1.id= :modelVersionId  )")
    Optional<ModelApprover> findMinOrderByModelIdAndApproverLogsEmpty(@Param("modelVersionId") Long modelVersionId);

    @Query(value = "select modelApprover from ModelApprover modelApprover " +
            " inner join modelApprover.modelMemberRole mmr" +
            " inner join mmr.modelMember mm" +
            " inner join mm.modelVersion mv" +
            " where  mv.id= :modelVersionId" +
            " and modelApprover.status='ACTIVE'" +
            " and modelApprover.id not in  (" +
            "  select mvp.id from ApproverLog log " +
            "   inner join log.approveState codeCat" +
            "   inner join log.modelApprover mvp" +
            "   inner join mvp.modelMemberRole mmr" +
            "   inner join mmr.modelMember mm" +
            "   inner join mm.modelVersion mv" +
            "   where mv.id=:modelVersionId " +
            "    and codeCat.code in (:statusList) " +
            "    and log.status='ACTIVE'" +
            "    and mvp.id=modelApprover.id)")
    List<ModelApprover> findAllNotCheckModelApprover(Long modelVersionId, List<String> statusList);

    @Query(value = "select new com.hamsoo.bpm.model.dto.model.ModelDto(model,mv,log,org,codeApproveLevel) from  " +
            " ApproverLog log" +
            " inner join log.approveState codeLogState" +
            " inner join log.modelApprover apprv" +
            " inner join apprv.level codeApproveLevel" +
            " inner join apprv.modelMemberRole mmr" +
            " inner join mmr.modelMember mem" +
            " inner join mem.memberType mt" +
            " inner join mmr.modelRole mrole " +
            " inner join User u on u.id=mem.memberId and mt.code='USER' " +
            " inner join mem.modelVersion mv " +
            " inner join mv.model model" +
            " inner join model.modelType codeModelType" +
            " inner join model.organ org" +
            " inner join mv.status codeModelVersionStatus" +
            " where org.id=:curOrg and " +
            " model.status='ACTIVE' and " +
            " u.id= :curUser and" +
            " codeModelVersionStatus.code=:modelVersionStatus  and " +
            " mrole.code=:modelRole  and" +
            " mv.lastVersion=true  and " +
            " not exists (select log1 from ApproverLog log1" +
            " where log1.modelApprover.id=apprv.id and log1.status='ACTIVE' and " +
            " (log1.approveState.code='APPROVE' or log1.approveState.code='REJECTY'  or log1.approveState.code='ACKNOWLEDGE')) and " +
            " codeLogState.code in (:approveLogState)  and" +
            " ((:approveLevel) is null or codeApproveLevel.code in  (:approveLevel) ) and " +
            " (:modelName is null or model.name like %:modelName% ) and " +
            " ((:modelTypeCode) is null or codeModelType.code in (:modelTypeCode) )" +
            " order by log.createdDate desc")
    Page<ModelDto> findAllModelApproveListForUser(Pageable pageable,
                                                  Long curOrg, Long curUser,
                                                  String modelVersionStatus,
                                                  String modelRole,
                                                  List<String> approveLogState,
                                                  String modelName,
                                                  List<String> modelTypeCode,
                                                  List<String> approveLevel
    );
}
