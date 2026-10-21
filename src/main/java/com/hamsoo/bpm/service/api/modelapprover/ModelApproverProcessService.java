package com.hamsoo.bpm.service.api.modelapprover;

import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.modelapprover.StartProcessResDTO;

public interface ModelApproverProcessService {
    StartProcessResDTO startProcess(Long modelVersionId, Boolean forceStart);

    ModelApproverDto approveModel(Long modelVersionId, ModelApproverDto modelApprover);

    ModelApproverDto deferModel(Long modelVersionId, ModelApproverDto modelApproverDto);

    ModelApproverDto acknowledgeModel(Long modelVersionId, ModelApproverDto modelApproverDto);
}
