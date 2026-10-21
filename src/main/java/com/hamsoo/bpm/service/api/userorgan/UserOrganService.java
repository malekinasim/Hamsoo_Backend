package com.hamsoo.bpm.service.api.userorgan;

import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.repository.jpa.userorgan.UserOrganRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface UserOrganService extends BaseService<UserOrgan, Long, UserOrganRepository> {

    List<UserDto> findAllUserOfOrgan();

    List<UserOrganDto> findAllUserOfOrgan(Long Id);


    UserOrgan findByIdOrElseThrow(Long id);

    Set<UserOrgan> findAllOrganOfUser(User user);


    UserOrganDto mapToDto(UserOrgan userOrgan);

    UserOrgan getOrElseThrow(Long userId, Long organId);

    List<UserOrgan> findAllByUserAndOrgan(Long userId, Long organId);

    OrganDto getCurUserOrgan(String curOrgan, String username);

    UserOrgan getUserPrivateOrganObj(Account account, User user);

    List<UserOrgan> createAll(List<UserOrganDto> userOrganDto);

    UserOrgan create(UserOrganDto userOrganDto);

    void passiveOrganMemberAndModelMemberOfOrgan(Long id);

    SearchDto list(Long organId, int page, int size, String filter, String orderBy, Sort.Direction direction);

    List<UserOrgan> findAllByPersonId(Long id);

    UserOrgan edit(UserOrganDto userOrganDto);

    void passiveAllOrganMemberAndModelMemberOfOrgan(List<UserOrganDto> passiveUserOrganDtos);

    List<User> getAdminUsersOfOrgan(Long valueOf);


    Integer countUserOrganByOrganId(Long organId);
}