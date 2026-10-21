package com.hamsoo.bpm.service.impl.userfavorite;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userfavorite.UserFavoriteService;
import com.hamsoo.bpm.repository.jpa.userfavorite.UserFavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserService userService;

    public UserFavoriteServiceImpl(UserFavoriteRepository userFavoriteRepository,
                                   UserService userService) {
        this.userFavoriteRepository = userFavoriteRepository;
        this.userService = userService;
    }

    @Override
    public UserFavoriteRepository getRepository() {
        return userFavoriteRepository;
    }

    @Override
    public UserFavorite findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.userFavorite.idIsNotValid");
        }
        return getRepository().findById(id)
                .orElseThrow(() -> new BusinessException("error.userFavorite.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserFavorite create(UserFavoriteDto userFavoriteDto) {
        Assert.notNull(userFavoriteDto, "userFavoriteDto is null");
        List<UserFavorite> userFavoriteList = getRepository().findByReferenceTypeAndReferenceIdAndUserId(userFavoriteDto.getReferenceType(),
                userFavoriteDto.getReferenceId(), SecurityUtils.getCurrentUserOrElseThrow().getId());
        if (!CollectionUtils.isEmpty(userFavoriteList)) {
            throw new BusinessException("error.userFavorite.favoriteRequestIsDuplicate");
        }
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setReferenceType(userFavoriteDto.getReferenceType());
        userFavorite.setReferenceId(userFavoriteDto.getReferenceId());
        userFavorite.setUser(userService.findByIdOrElseThrow(SecurityUtils.getCurrentUserOrElseThrow().getId()));
        return save(userFavorite);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id, boolean checkUser) {
        UserFavorite userFavorite = findByIdOrElseThrow(id);
        if (checkUser && !userFavorite.getUser().getId().equals(SecurityUtils.getCurrentUserOrElseThrow().getId())) {
            throw new BusinessException("error.userFavorite.userJustCanDeleteOwnFavorite");
        }
        delete(userFavorite);
    }

    @Override
    @Transactional(readOnly = true)
    public UserFavoriteDto mapToDto(UserFavorite userFavorite) {
        Assert.notNull(userFavorite, "userFavorite is null");
        userFavorite = findByIdOrElseThrow(userFavorite.getId());
        UserFavoriteDto userFavoriteDto = new UserFavoriteDto();
        userFavoriteDto.setId(userFavorite.getId());
        userFavoriteDto.setReferenceType(userFavorite.getReferenceType());
        userFavoriteDto.setReferenceId(userFavorite.getReferenceId());
        userFavoriteDto.setUserDto(userService.mapToDto(userFavorite.getUser()));
        return userFavoriteDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserId(String referenceType, Long referenceId, Long userId) {
        return getRepository().findByReferenceTypeAndReferenceIdAndUserId(referenceType, referenceId, userId);
    }


    @Override
    public List<UserFavorite> findByReferenceTypeAndReferenceIdAndUserListId(String referenceType, Long modelId, List<Long> userList) {
        return getRepository().findByReferenceTypeAndReferenceIdAndUserListId(referenceType, modelId, userList);
    }
}
