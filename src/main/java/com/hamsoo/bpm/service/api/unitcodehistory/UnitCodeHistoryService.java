package com.hamsoo.bpm.service.api.unitcodehistory;

import com.hamsoo.bpm.model.dto.unitcodehistory.UnitCodeHistoryDto;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.repository.jpa.unitcodehistory.UnitCodeHistoryRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.Optional;

public interface UnitCodeHistoryService extends BaseService<UnitCodeHistory, Long, UnitCodeHistoryRepository> {

    UnitCodeHistory findByIdOrElseThrow(Long id);

    UnitCodeHistory create(UnitCodeHistoryDto unitCodeHistoryDto);

    void passive(Long id);

    Optional<UnitCodeHistory> findActiveUnitCodeHistory(Long unitId, Long organId);

    UnitCodeHistory findActiveUnitCodeHistoryOrElseThrow(Long unitId, Long organId);

}
