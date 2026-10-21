package com.hamsoo.bpm.service.api.modeldocument;

import com.hamsoo.bpm.model.dto.modeldocument.ModelDocumentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument;
import com.hamsoo.bpm.repository.jpa.modeldocument.ModelDocumentRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.List;

public interface ModelDocumentService extends BaseService<ModelDocument, Long, ModelDocumentRepository> {

    ModelDocument findByIdOrElseThrow(Long id);

    ModelDocument create(ModelDocumentDto modelDocumentDto);

    void delete(Long id);

    ModelDocumentDto mapToDto(ModelDocument modelDocument);

    SearchDto list(ModelDocumentDto modelDocumentDto, int page, int size, String orderBy, Sort.Direction direction);

    List<ModelDocument> findByModelVersionId(Long modelVersionId);

    void copyModelDocumentsIfExist(Long newModelVersionId, Long oldModelVersionId);

    void passiveByElementIdAndModelVersion(Long modelVersionId, String elementId);

    BigInteger getOrganModelDocumentCapacity(Long id);
}
