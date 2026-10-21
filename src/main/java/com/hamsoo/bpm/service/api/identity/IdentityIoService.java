package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.identity.IdentityIoDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.repository.jpa.identity.IdentityIoRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IdentityIoService extends BaseService<IdentityIo, Long, IdentityIoRepository> {

    IdentityIo findByIdOrElseThrow(Long id);

    IdentityIo create(IdentityIoDto identityIoDto);

    Optional<IdentityIo> findEqualActiveIdentityIo(IdentityIoDto identityIoDto);

    void passive(Long id);

    Optional<ErrorDto> validateIdentityIoCanPassive(Long id);

    IdentityIoDto mapToDto(IdentityIo identityIo);

    List<IdentityIoDto> mapToDto(List<IdentityIo> identityIoList);

    IdentityIoDto mapToDtoWithAdditionalData(IdentityIo identityIo);

    List<ErrorDto> validateIdentityIo(IdentityIo identityIo);

    List<ErrorDto> validateIdentityIo(List<IdentityIo> identityIoList);

    SearchDto list(IdentityIoDto identityIoDto, int page, int size, String orderBy, Sort.Direction direction, Function<IdentityIo, IdentityIoDto> mapper);

    List<IdentityIo> findActiveByModelVersionId(Long modelVersionId);

    IdentityIo clone(IdentityIo identityIo);

    Long countByIoCatalogId(Long ioCatalogId);

    List<IdentityIo> findInternalActiveByModelVersionId(String typeCode, Long modelVersionId);

    void copyIoIdentityAndIoSupCust(Long modelVersionIdFrom, Long modelVersionIdTo);
}
