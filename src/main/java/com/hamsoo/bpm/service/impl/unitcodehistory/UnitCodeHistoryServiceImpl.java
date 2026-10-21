package com.hamsoo.bpm.service.impl.unitcodehistory;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.unitcodehistory.UnitCodeHistoryDto;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.unitcodehistory.UnitCodeHistoryService;
import com.hamsoo.bpm.repository.jpa.unitcodehistory.UnitCodeHistoryRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UnitCodeHistoryServiceImpl implements UnitCodeHistoryService {

    private final UnitCodeHistoryRepository unitCodeHistoryRepository;
    private final UnitService unitService;

    public UnitCodeHistoryServiceImpl(UnitCodeHistoryRepository unitCodeHistoryRepository,
                                      @Lazy UnitService unitService) {
        this.unitCodeHistoryRepository = unitCodeHistoryRepository;
        this.unitService = unitService;
    }

    @Override
    public UnitCodeHistoryRepository getRepository() {
        return unitCodeHistoryRepository;
    }

    @Override
    public UnitCodeHistory findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.unitCodeHistory.idIsNotValid");
        }
        return getRepository().findActiveByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.unitCodeHistory.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UnitCodeHistory create(UnitCodeHistoryDto unitCodeHistoryDto) {
        Assert.notNull(unitCodeHistoryDto, "unitCodeHistoryDto is null");
        List<UnitCodeHistory> unitList = getRepository()
                .findActiveByOrganIdAndCode(SecurityUtils.getCurrentOrganIdOrElseThrow(), unitCodeHistoryDto.getCode());
        if (!CollectionUtils.isEmpty(unitList)) {
            throw new BusinessException("error.unitCodeHistory.codeIsDuplicate");
        }
        UnitCodeHistory unitCodeHistory = new UnitCodeHistory();
        unitCodeHistory.setCode(unitCodeHistoryDto.getCode());
        unitCodeHistory.setValidFrom(LocalDateTime.now());
        unitCodeHistory.setStatus(Status.ACTIVE.name());
        unitCodeHistory.setUnit(unitService.findByIdOrElseThrow(unitCodeHistoryDto.getUnitId()));
        return save(unitCodeHistory);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        UnitCodeHistory unitCodeHistory = findByIdOrElseThrow(id);
        unitCodeHistory.setStatus(Status.PASSIVE.name());
        unitCodeHistory.setValidTo(LocalDateTime.now().minusSeconds(1));
        save(unitCodeHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitCodeHistory> findActiveUnitCodeHistory(Long unitId, Long organId) {
        return getRepository().findActiveUnitCodeHistory(unitId, organId);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitCodeHistory findActiveUnitCodeHistoryOrElseThrow(Long unitId, Long organId) {
        return findActiveUnitCodeHistory(unitId, organId)
                .orElseThrow(() -> new BusinessException("error.unit.activeUnitCodeHistoryNotFound"));
    }

}
