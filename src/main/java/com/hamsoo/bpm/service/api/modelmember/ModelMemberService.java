package com.hamsoo.bpm.service.api.modelmember;

import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.repository.jpa.modelmember.ModelMemberRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ModelMemberService extends BaseService<ModelMember, Long, ModelMemberRepository> {

    ModelMember findByIdOrElseThrow(Long id);

    ModelMember create(ModelMemberDto modelMemberDto);

    ModelMember passive(Long id);

    void passiveAndDeleteModelFavorite(Long id);

    ModelMemberDto mapToDto(ModelMember modelMember);

    @Transactional(readOnly = true)
    ModelMemberDto mapToDtoByOnlineInfo(ModelMember modelMember);

    List<UserDto> findAllOnlineUserOfModelVersion(Long modelVersionId);

    List<ModelMemberDto> mapToDto(List<ModelMember> modelMemberList);

    void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId);

    List<ModelMember> findByModelVersionIdAndModelRoleCode(Long modelVersionId, String modelRoleCode);

    List<ModelMember> findByModelIdAndModelRoleCodeAndUserId(Long modelId, String modelRoleCode, Long userId);

    List<ModelMember> findByModelIdAndMemberIdAndMemberTypeId(Long modelId, Long memberId,Long memberTypeId);

    List<ModelMember> getUserModelMember(Long userId, Long modelVersionId);



    boolean isUserMemberOfModel(Long userId, Long modelVersionId);


    List<User> findByModelVersionId(Long modelVersionId);

    List<User> getMembersOfModel(Long modelVersionId);


    int getCountAllLastVersionByUserId(Long userId);

}
