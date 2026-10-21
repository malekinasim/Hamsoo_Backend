package com.hamsoo.bpm.repository.jpa.modelmemberrole;

import com.hamsoo.bpm.model.dto.modelmember.ModelMemberUserDto;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelMemberRoleRepository extends HamsooRepository<ModelMemberRole, Long> {


    @Query(value = "select mmr from ModelMemberRole mmr inner join mmr.modelMember mm" +
            " inner join mm.modelVersion mv inner join mv.model m inner join m.organ o " +
            "where mmr.id= :id and mm.status =:status and o.id= :organId " +
            " and m.status = :status and  mmr.status=:status"
    )
    Optional<ModelMemberRole> findByIdAndStatusAndOrganId(Long id, String status, Long organId);

    @Query(value = "select mm from ModelMember mm" +
            " inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "where mv.id= :modelVersionId and mm.status =:status and o.id= :organId " +
            " and m.status = :status " +
            " and mm.memberId=:memberId" +
            " and mt.code=:memberType")
    Optional<ModelMember> findByModelVersionIdAndOrganAndMemberIdAndMemberType(Long modelVersionId, Long memberId, String memberType, String status, Long organId);


    @Query(value = "select mmr from ModelMemberRole mmr inner join mmr.modelMember mm" +
            " inner join mm.modelVersion mv inner join mv.model m inner join m.organ o " +
            "where mm.id= :modelMemberId and mm.status =:status and o.id= :organId " +
            " and m.status = :status and  mmr.status=:status")
    List<ModelMemberRole> findByModelMemberId(Long modelMemberId, String status, Long organId);


    @Query(value = "select mmr from ModelMemberRole mmr " +
            "where " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId and mt.code='POST' " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId and mt.code='UNIT' " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and un.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId and mt.code='JOB' " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and jb.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId)"
    )
    List<ModelMemberRole> findByModelVersionIdAndUserIdAndStatus(Long modelVersionId, Long userId, Long organId, String status);

    @Query(value = "select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            "inner join mm.modelVersion mv " +
            "inner join mv.model m inner join m.organ o " +
            "inner join mmr.modelRole r " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and m.status =:status and  mmr.status=:status " +
            "and mm.memberId=:memberId " +
            "and mt.code=:memberType " +
            "and r.id=:modelRoleId")
    List<ModelMemberRole> findByModelVersionIdAndUserIdAndRoleIdAndStatus(Long modelVersionId, Long memberId, String memberType, Long organId, Long modelRoleId, String status);


    @Query(value = "select mmr from ModelMemberRole mmr " +
            "where " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId and mt.code='POST' " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId and mt.code='UNIT' " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and un.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId) or " +
            "mmr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId and mt.code='JOB' " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where  mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and jb.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId)"
    )
    List<ModelMemberRole> findByUserIdAndStatusAndOrganId(Long userId, String status, Long organId);

    @Query(value = "select modelmemr from ModelMemberRole modelmemr " +
            "where " +
            "modelmemr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelRole r " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join User u on u.id=mm.memberId and mt.code='USER' " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and m.status =:status " +
            "and  mmr.status=:status " +
            "and u.id=:userId " +
            "and r.id=:modelRoleId) or " +
            "modelmemr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelRole r " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Post p on p.id=mm.memberId and mt.code='POST' " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId " +
            "and r.id=:modelRoleId ) or " +
            "modelmemr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelRole r " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Unit un on un.id=mm.memberId and mt.code='UNIT' " +
            "inner join un.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and un.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId " +
            "and r.id=:modelRoleId) or " +
            "modelmemr.id in " +
            "(select mmr from ModelMemberRole mmr " +
            "inner join mmr.modelRole r " +
            "inner join mmr.modelMember mm " +
            "inner join mm.memberType mt " +
            " inner join mm.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join o.userOrgans uo " +
            "inner join uo.person pr1 " +
            "inner join uo.user u " +
            "inner join Job jb on jb.id=mm.memberId and mt.code='JOB' " +
            "inner join jb.posts p " +
            "inner join p.postMembers pstmem " +
            "inner join pstmem.person pr " +
            "where mv.id= :modelVersionId " +
            "and mm.status =:status " +
            "and o.id= :organId " +
            "and pr.id= pr1.id " +
            "and m.status =:status " +
            "and uo.status =:status " +
            "and jb.status =:status " +
            "and p.status =:status " +
            "and pstmem.status =:status " +
            "and pr.status =:status " +
            "and mmr.status=:status " +
            "and u.id=:userId)"
    )
    List<ModelMemberRole> findByModelVersionIdAndRoleIdAndStatus(Long modelVersionId, Long organId, Long modelRoleId, String status);

    @Query(value = " select mem.user_id as userId,   " +
            "             mem.name as name ,   " +
            "             mem.family as family ,   " +
            "             mem.national_code as nationalCode ,   " +
            "             mem.image_id as imageId,   " +
            "             String_AGG(distinct mem.role_name||':'||mem.role_code,',') roles ,   " +
            "             String_AGG(distinct mem.mem_name||':'||mem.mem_type,',') members ,   " +
            "             String_AGG(distinct mem.role_name,',')  roleNames    " +
            "                from  " +
            "                (  select distinct  tbl.* from  ( " +
            "                    (select u.user_id,u.name,u.family,u.national_code,u.image_id,r.name role_name,r.code role_code,u.name||' '||u.family mem_name ,mt.code mem_type    " +
            "                                     from bpm.bpm_model_members mm    " +
            "                                     inner join bpm.bpm_code_category mt on mm.member_type = mt.code_category_id   " +
            "                                     inner join bpm.bpm_model_member_roles mmr on mm.model_member_id =mmr.model_member_id   " +
            "                                     inner join bpm.bpm_model_roles r on r.model_role_id =mmr.model_role_id   " +
            "                                     inner join bpm.bpm_users u on mm.member_id=u.user_id   " +
            "                                     inner join bpm.bpm_user_organs ou on ou.user_id=u.user_id   " +
            "                                     inner join bpm.bpm_model_versions mv on mv.model_version_id= mm.model_id   " +
            "                                     inner join bpm.bpm_models m on m.model_id=mv.model_id   " +
            "                                     inner join bpm.bpm_organs o on o.organ_id=m.organ_id   " +
            "                                     where mt.code='USER' and    " +
            "                                   mv.model_version_id=:modelVersionId and    " +
            "                                     o.organ_id=:organId and   " +
            "                                     m.status='ACTIVE' and    " +
            "                                     mmr.status='ACTIVE' and   " +
            "                                     mm.status='ACTIVE' and   " +
            "                                     mv.last_Version=true  and r.code!='MODEL_APPROVER'   " +
            "                 ) union   " +
            "                 (select u.user_id,u.name,u.family,u.national_code,u.image_id,r.name role_name,r.code role_code,p.name mem_name ,mt.code mem_type from bpm.bpm_model_members mm    " +
            "                                     inner join bpm.bpm_code_category mt on mm.member_type = mt.code_category_id   " +
            "                                     inner join bpm.bpm_posts p on mm.member_id=p.post_id   " +
            "                                     inner join bpm.bpm_model_member_roles mmr on mm.model_member_id =mmr.model_member_id   " +
            "                                     inner join bpm.bpm_model_roles r on r.model_role_id =mmr.model_role_id   " +
            "                                     inner join bpm.bpm_post_members pstmem on pstmem.post_id=p.post_id   " +
            "                                     inner join bpm.bpm_persons prs on pstmem.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_model_versions mv on mv.model_version_id= mm.model_id   " +
            "                                     inner join bpm.bpm_models m on m.model_id=mv.model_id   " +
            "                                     inner join bpm.bpm_organs o on o.organ_id=m.organ_id   " +
            "                                     inner join bpm.bpm_user_organs ou on o.organ_id=ou.organ_id   " +
            "                                     and ou.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_users u on u.user_id=ou.user_id   " +
            "                                     where mt.code='POST' and    " +
            "                                     o.organ_id=:organId and   " +
            "                                   mv.model_version_id=:modelVersionId and    " +
            "                                     m.status='ACTIVE' and    " +
            "                                     mmr.status='ACTIVE' and   " +
            "                                     mm.status='ACTIVE' and   " +
            "                                     mv.last_Version=true   and r.code!='MODEL_APPROVER'   " +
            "                 )union   " +
            "                 (select u.user_id,u.name,u.family,u.national_code ,u.image_id,r.name role_name,r.code role_code,un.name mem_name ,mt.code mem_type from bpm.bpm_model_members mm    " +
            "                                     inner join bpm.bpm_code_category mt on mm.member_type = mt.code_category_id   " +
            "                                     inner join bpm.bpm_units un on mm.member_id=un.unit_id   " +
            "                                     inner join bpm.bpm_model_member_roles mmr on mm.model_member_id =mmr.model_member_id   " +
            "                                     inner join bpm.bpm_model_roles r on r.model_role_id =mmr.model_role_id   " +
            "                                     inner join bpm.bpm_posts p on un.unit_id=p.unit_id   " +
            "                                     inner join bpm.bpm_post_members pstmem on pstmem.post_id=p.post_id   " +
            "                                     inner join bpm.bpm_persons prs on pstmem.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_model_versions mv on mv.model_version_id= mm.model_id   " +
            "                                     inner join bpm.bpm_models m on m.model_id=mv.model_id   " +
            "                                     inner join bpm.bpm_organs o on o.organ_id=m.organ_id    " +
            "                                     inner join bpm.bpm_user_organs ou on o.organ_id=ou.organ_id   " +
            "                                     and ou.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_users u on u.user_id=ou.user_id   " +
            "                                     where mt.code='UNIT' and    " +
            "                                     o.organ_id=3 and   " +
            "                                    mv.model_version_id=:modelVersionId and    " +
            "                                      m.status='ACTIVE' and    " +
            "                                     mmr.status='ACTIVE' and   " +
            "                                     mm.status='ACTIVE' and   " +
            "                                     mv.last_Version=true  and r.code!='MODEL_APPROVER'   " +
            "                 )  union   " +
            "                 (select u.user_id,u.name,u.family,u.national_code,u.image_id, r.name role_name,r.code role_code,jb.name mem_name ,mt.code mem_type  from bpm.bpm_model_members mm    " +
            "                                     inner join bpm.bpm_code_category mt on mm.member_type = mt.code_category_id   " +
            "                                     inner join bpm.bpm_model_member_roles mmr on mm.model_member_id =mmr.model_member_id   " +
            "                                     inner join bpm.bpm_model_roles r on r.model_role_id =mmr.model_role_id   " +
            "                                     inner join bpm.bpm_jobs jb on mm.member_id=jb.job_id   " +
            "                                     inner join bpm.bpm_posts p on jb.job_id=p.job_id   " +
            "                                     inner join bpm.bpm_post_members pstmem on pstmem.post_id=p.post_id   " +
            "                                     inner join bpm.bpm_persons prs on pstmem.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_model_versions mv on mv.model_version_id= mm.model_id   " +
            "                                     inner join bpm.bpm_models m on m.model_id=mv.model_id   " +
            "                                     inner join bpm.bpm_organs o on o.organ_id=m.organ_id   " +
            "                                     inner join bpm.bpm_user_organs ou on o.organ_id=ou.organ_id   " +
            "                                     and ou.person_id=prs.person_id   " +
            "                                     inner join bpm.bpm_users u on u.user_id=ou.user_id   " +
            "                                     where mt.code='JOB' and    " +
            "                                     o.organ_id=:organId and   " +
            "                                    mv.model_version_id=:modelVersionId and    " +
            "                                      m.status='ACTIVE' and    " +
            "                                     mmr.status='ACTIVE' and   " +
            "                                     mm.status='ACTIVE' and   " +
            "                                     mv.last_Version=true   and r.code!='MODEL_APPROVER'   " +
            "                  )) as tbl ) as  mem     " +
            "                " +
            "                  GROUP BY   " +
            "                     mem.user_id,mem.name,mem.name,mem.family,mem.national_code,mem.image_id ", nativeQuery = true
    )
    List<ModelMemberUserDto> FindAllModelUserMember(Long modelVersionId, Long organId);
}
