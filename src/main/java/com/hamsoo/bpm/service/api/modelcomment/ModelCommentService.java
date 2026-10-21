package com.hamsoo.bpm.service.api.modelcomment;

import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelcomment.ModelComment;
import com.hamsoo.bpm.repository.jpa.modelcomment.ModelCommentRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ModelCommentService extends BaseService<ModelComment, Long, ModelCommentRepository> {

    ModelComment findByIdOrElseThrow(Long id);

    ModelComment create(ModelCommentDto modelCommentDto);

    ModelComment edit(ModelCommentDto modelCommentDto);

    void passive(Long id);

    ModelCommentDto mapToDto(ModelComment modelComment);

    List<ModelCommentDto> mapToDto(List<ModelComment> modelCommentList);

    SearchDto list(Long modelVersionId, ModelCommentDto modelCommentDto, int page, int size, String orderBy, Sort.Direction direction);

    void updateModelVersionId(Long newModelVersionId, Long oldModelVersionId);

    Page<ModelCommentDto> getCommentAndElementCommentListOfLastVersion(Long modelVersionId, Pageable pageable);

    Page<ModelCommentDto> getElementCommentListOfLastVersion(Long modelVersionId, String elementId, Pageable pageable);

    SearchDto getCommentAndElementCommentListOfLastVersionWithPageData(Long modelVersionId, Pageable pageable);

    List<ModelCommentDto> getAllElementCommentListOfLastVersion(Long modelVersionId, String elementId);
}
