package com.hamsoo.bpm.service.api.identity;

import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.identity.IoSupCustDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.identity.IoSupCust;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.repository.jpa.identity.IoSupCustRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IoSupCustService extends BaseService<IoSupCust, Long, IoSupCustRepository> {

    IoSupCust findByIdOrElseThrow(Long id);

    IoSupCust create(IoSupCustDto ioSupCustDto);

    Optional<IoSupCust> findEqualActiveIoSupCust(IoSupCustDto ioSupCustDto);

    void passive(Long id);

    Optional<ErrorDto> validateIoSupCustCanPassive(Long id);

    IoSupCustDto mapToDto(IoSupCust ioSupCust);

    List<IoSupCustDto> mapToDto(List<IoSupCust> ioSupCustList);

    IoSupCustDto mapToDtoWithAdditionalData(IoSupCust ioSupCust);

    SearchDto list(IoSupCustDto ioSupCustDto, int page, int size, String orderBy, Sort.Direction direction, Function<IoSupCust, IoSupCustDto> mapper);

    List<String> getInternalProcessAsisTobeCodeList(ModelVersion modelVersion);

    SearchDto getInternalProcessList(Long modelVersionId, IoSupCustDto ioSupCustDto, List<String> asisTobeCodeList, String modelName, int page, int size);

    List<IoSupCust> findByIdentityIoId(Long identityIoId, String identityIoTypeCode, List<String> ioSupCustTypeCodeList);

    Long countByIdentityIoId(Long identityIoId, String identityIoTypeCode, List<String> ioSupCustTypeCodeList);

    Optional<IoSupCust> findActiveInternalIoSupCust(Long modelVersionId, String identityIoTypeCode, Long ioCatalogId, Long internalSupCustId,
                                                    List<String> modelVersionStatusCodeList);

    List<IoSupCust> findActiveInternalIoSupCust(Long modelVersionId);

    void checkUpdateInternalSupCust(ModelVersion newModelVersion, ModelVersion oldModelVersion);

    void updateInternalSupCust(Long newSupCustId, Long oldSupCustId);

    List<ErrorDto> validateIoSupCust(IoSupCust ioSupCust);

    List<ErrorDto> validateIoSupCust(List<IoSupCust> ioSupCustList);

    IoSupCust clone(IoSupCust ioSupCust);

    Long countByExternalProcessId(Long externalProcessId);

    Long countInternalSupCustByModelId(Long modelId);


    List<IoSupCust> findAllIoCatalogActiveInternalIoSupCust(Long modelVersionId,
                                                            Long ioCatalogId);
}
