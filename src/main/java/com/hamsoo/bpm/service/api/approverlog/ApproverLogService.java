package com.hamsoo.bpm.service.api.approverlog;


import com.hamsoo.bpm.model.dto.approverlog.ApproverLogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.repository.jpa.approverlog.ApproverLogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApproverLogService extends BaseService<ApproverLog, Long, ApproverLogRepository> {

    ApproverLog findByIdOrElseThrow(Long id);

    void deleteApproverLog(Long id);

    ApproverLog create(ApproverLogDto approverLogDto);

    Optional<ApproverLogDto> findLastLogForModelApprover(Long modelApproverId);

    SearchDto findAllByModelVersionId(long modelVersionId, Pageable pageable);
}