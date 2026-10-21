package com.hamsoo.bpm.service.api.modelapprover;

import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.repository.jpa.modelapprover.ModelApproverRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface ModelApproverService extends BaseService<ModelApprover, Long, ModelApproverRepository> {
    ModelApprover findByIdOrElseThrow(Long id);


    ModelApprover create(ModelApproverDto modelApproverDto);

    ModelApproverDto mapToDto(ModelApprover modelApprover);

    List<ModelApproverDto> list(Long modelVersionId, String approveGroup);

    ModelApproverDto edit(ModelApproverDto modelApproverDto);

    void passive(Long id);

    void passiveAll(List<Long> id);

    List<ModelApproverDto> generateDefaultModelApproverList(Long modelVersionId);

    List<ModelApproverDto> ChangeModelApproverOrder(Long modelVersionId, Long srcModelApproverDto, Long targetModelApproverDto);

    Optional<ModelApprover> getNextStepModelApprover(Long modelVersionId);

    void passiveAllNotCheckModelApprover(Long modelVersionId, Long modelApproveId);


    SearchDto getModelApproveListForUser(ModelApproverDto modelApproverDto, int page, int size);
}