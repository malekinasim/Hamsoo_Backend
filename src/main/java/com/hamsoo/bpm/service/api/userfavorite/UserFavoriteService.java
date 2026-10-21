package com.hamsoo.bpm.service.api.userfavorite;

import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.repository.jpa.userfavorite.UserFavoriteRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface UserFavoriteService extends BaseService<UserFavorite, Long, UserFavoriteRepository> {

    UserFavorite findByIdOrElseThrow(Long id);

    UserFavorite create(UserFavoriteDto userFavoriteDto);

    void delete(Long id, boolean checkUser);

    UserFavoriteDto mapToDto(UserFavorite userFavorite);

    List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserId(String referenceType, Long referenceId, Long userId);


    List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserListId(String name, Long modelId, List<Long> toList);
}
