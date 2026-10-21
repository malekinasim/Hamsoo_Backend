package com.hamsoo.bpm.service.api.modelelementreference;

import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.repository.jpa.modelelementrefrence.ModelElementRefrenceRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface ModelElementReferenceService extends BaseService<ModelElementRefrence, Long, ModelElementRefrenceRepository> {

    ModelElementRefrence findByIdOrElseThrow(Long id);


    void delete(Long id);

    ModelElementReferenceDto mapToDto(ModelElementRefrence modelElementRefrence);


    ModelElementReferenceDto mapToDtoWithoutReferenceInfo(ModelElementRefrence modelElementReference);

    ModelElementRefrence create(ModelElementReferenceDto modelElementReferenceDto);


    Optional<ModelElementRefrence> findByElementIdAndModelVersionIdAndModelRefrenceIdAndReferenceType(String elementId, Long modelVersionId, Long referenceId, String referenceType);

    void deleteAllReferenceByElementIdAndModelVersion(Long modelVersionId, String elementId);

    ModelElementRefrence edit(ModelElementReferenceDto modelElementReferenceDto);


    List<ModelElementReferenceDto> list(ModelElementReferenceDto modelElementReferenceDto);

    Optional<ModelElementRefrence> findByModelReferenceIdAndReferenceType(Long modelRefrenceId, String referenceType);

    SearchDto getInternalProcessList(Long modelVersionId, ModelElementReferenceDto modelElementReferenceDto, String modelName, int page, int size);

    List<ModelElementRefrence> findLockedByModelVersionId(Long modelVersionId);

    List<ModelElementRefrence> findLastByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId);

    List<ModelElementRefrence> findLastLockedByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId);

    void updateInternalReferenceId(Long newReferenceId, Long oldReferenceId);

    void updateInternalReferenceIdWithLock(Long newModelReferenceId, Long oldModelReferenceId);

    ModelElementRefrence clone(ModelElementRefrence modelElementRefrence);

    void copyModelElementRefrenceIfExist(Long modelVersionIdFrom, Long modelVersionIdTo);

    Long countByReferencedModelId(Long referencedModelId);

    Long countByReferenceTypeCodeAndReferenceId(String referenceTypeCode, Long referenceId);

}
