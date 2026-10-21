package com.hamsoo.bpm.service.api.modelelementreference;

import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import org.springframework.data.domain.Pageable;

public interface ModelElementCommentService {

    ModelCommentDto create(ModelCommentDto modelCommentDto);

    void delete(Long id);


    SearchDto getElementCommentListOfLastVersionWithPageData(Long modelVersionId, String elementId, Pageable pageable);

    void deleteAllModelElementCommentByModelVersionIdAndElementId(Long modelVersionId, String elementId);
}
