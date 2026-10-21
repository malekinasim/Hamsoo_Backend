package com.hamsoo.bpm.service.api.organ;

import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.repository.jpa.organ.OrganRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface OrganService extends BaseService<Organ, Long, OrganRepository> {
    Organ findByIdOrElseThrow(Long id);

    Organ getUserPrivateOrganObj(Account account);

    void deleteOrgan(Long id);

    Organ findByNameOrElseThrow(String defaultOrgan);

    Optional<Organ> getDefualtUserOrgan(String username);

    OrganDto mapToDto(Organ organ);

    OrganDto mapToDtoByOrganMemberRole(Organ organ);

    Optional<Organ> findByIdAndUserName(@NotNull String curOrgan, String username);

    Organ create(OrganDto organDto, MultipartFile multipartFile);

    Organ edit(OrganDto organDto, MultipartFile multipartFile);

    void passive(Long id);

    SearchDto list(OrganDto organDto, int page, int size, String orderBy, Sort.Direction direction);


    List<OrganDto> getUserOrgans(Long userId);

    Integer getOrganModelCount(Long id);

    BigInteger getOrganModelCapacity(Long id);

    Integer getOrganUsersCount(Long organId);


}