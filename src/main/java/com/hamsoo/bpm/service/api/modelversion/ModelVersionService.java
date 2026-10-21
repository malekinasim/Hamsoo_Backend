package com.hamsoo.bpm.service.api.modelversion;

import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.repository.jpa.modelversion.ModelVersionRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ModelVersionService extends BaseService<ModelVersion, Long, ModelVersionRepository> {
    ModelVersion findByIdOrElseThrow(Long id);

    ModelVersion findLockedByIdAndOrganId(Long id);

    void deleteModelVersion(Long id);

    ModelVersion create(ModelVersionDto modelVersionDto);

    ModelVersion renameMilestoneName(Long id, ModelVersionDto modelVersionDto);

    ModelVersionDto mapToDto(ModelVersion modelVersion);

    ModelVersionDto mapToDtoWithRefData(ModelVersion modelVersion);

    ModelVersionDto mapToFilesData(ModelVersion modelVersion);

    List<ModelVersionDto> mapToDto(List<ModelVersion> modelVersionList);

    ModelVersionDto mapToIdName(ModelVersion modelVersion);

    List<ModelVersionDto> mapToIdName(List<ModelVersion> modelVersionList);

    Long updateXml(MultipartFile file, Long id);

    FilesDto getXml(Long id);

    Long updateImage(MultipartFile file, Long id);

    ModelVersion updateXmlAndImage(MultipartFile xml, MultipartFile image, Long id);

    FilesDto getImage(Long id);

    List<ModelVersion> getByResourceSchemaResourceCatalogId(Long resourceCatalogId);

    byte[] exportSipocReport(Long id);

    Optional<ModelVersion> findByModelIdAndAsisTobeCode(Long modelId, String asisTobeCode);

    ModelVersion getLastVersion(String asisTobe, Long modelId);

    Optional<ModelVersion> getLastVersionByParent(String asisTobe, Long modelId, Long mvParentId);

    List<ModelVersion> getLastVersions(Long modelId);

    List<ModelVersion> findByModelIdAndModelStatusCodeNot(Long modelId, String modelStatusCode);

    Page<ModelVersion> getInternalProcessList(List<String> asisTobeCodeList, List<Long> notInModelVersionIdList, Long organId,
                                              String modelName, List<String> modelTypeCodeList, Boolean filterHasReference, Pageable pageable);

    ModelVersion getLastVersion(Long modelVersionId);

    SearchDto milestoneList(ModelVersionDto modelVersionDto, int page, int size, String orderBy, Sort.Direction direction);

    List<ErrorDto> validateModelVersion(Long modelVersionId);

    ModelVersion updateStatus(Long id, String status);

    Long getCountByProcessCategoryId(Long processCategoryId);

    SearchDto listInvalidModels(ModelVersionDto modelDto, int page, int size, String orderBy, Sort.Direction direction);

    ModelVersionDto countInvalidModels(ModelVersionDto modelVersion);

    Optional<ModelVersion> findModelVersionOfModelApprover(Long modelApproverId);

    Optional<ModelVersion> findModelVersionOfModelElementReferenceId(Long modelElementCommentId);

    void checkModelVersionStatus(Long modelVersionId);

    ModelElementRefrence findModelVersionOfProcessCategoryModelElement(Long id);

    Optional<ModelVersion> findModelVersionOfModelMemberRole(Long id);

    ModelVersion updateWorkFlow(MultipartFile workFlow, Long id);

}
