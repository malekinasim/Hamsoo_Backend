package com.hamsoo.bpm.service.api.modelelementreference;

import com.hamsoo.bpm.model.dto.modelelementereference.ModelAssigneeDto;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.repository.jpa.modelelementrefrence.ModelElementRefrenceRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

public interface ModelElementTaskAssigneeService extends BaseService<ModelElementRefrence, Long, ModelElementRefrenceRepository> {
    ModelAssigneeDto create(ModelAssigneeDto modelTaskAssigneeDto);

    void editModelLanAssignee(String laneId, Long modelVersionId);

    void editAllModelLaneAssignee(Long modelVersionId);

    void deleteAllModelElementAssigneeByModelVersionIdAndElementId(Long modelVersionId, String elementId);

    ModelAssigneeDto getElementTaskAssigneeListOfLastVersion(Long modelVersionId, String elementId);

    void delete(Long id);

    ModelAssigneeDto edit(ModelAssigneeDto modelTaskAssigneeDto);
}
