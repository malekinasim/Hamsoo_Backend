package com.hamsoo.bpm.service.api.modelversion;

import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

public interface MilestoneService {

    ModelVersion milestone(Long modelVersionId, ModelVersionDto input);

    ModelVersion createTobeFromAsis(Long modelVersionId);

    ModelVersion operateTobeModel(Long modelVersionId);

    ModelVersion restoreToOldVersion(Long oldModelVersionId);

}
