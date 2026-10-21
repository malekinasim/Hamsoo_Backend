package com.hamsoo.bpm.service.impl.approvelog;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.approverlog.ApproverLogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.repository.jpa.approverlog.ApproverLogRepository;
import com.hamsoo.bpm.service.api.approverlog.ApproverLogService;
import com.hamsoo.bpm.service.api.codecategory.CodeCategoryService;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApproverLogServiceImpl implements ApproverLogService {
    public ApproverLogRepository repository;
    private CodeCategoryService codeCategoryService;
    private ModelApproverService modelApproverService;

    public ApproverLogServiceImpl(ApproverLogRepository repository,
                                  CodeCategoryService codeCategoryService,
                                  @Lazy ModelApproverService modelApproverService) {
        this.repository = repository;
        this.codeCategoryService = codeCategoryService;
        this.modelApproverService = modelApproverService;
    }

    @Override
    public ApproverLogRepository getRepository() {
        return repository;
    }

    @Override
    public ApproverLog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.approverLog.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.approverLog.idIsNotValid"));
    }

    @Override
    public void deleteApproverLog(Long id) {
        ApproverLog approverLog = findByIdOrElseThrow(id);
        delete(approverLog);
    }

    @Override
    public ApproverLog create(ApproverLogDto approverLogDto) {
        Assert.notNull(approverLogDto, "approverLogDto is null");
        ApproverLog approverLog = new ApproverLog();
        approverLog.setApproveState(codeCategoryService.findByCategoryAndCode(ApproverLog.ApproveLogState.CATEGORY,
                approverLogDto.getApproveStateCode()));
        approverLog.setModelApprover(modelApproverService.findByIdOrElseThrow(approverLogDto.getModelApproverId()));
        approverLog.setStateDate(LocalDateTime.now());
        return save(approverLog);

    }

    @Override
    public Optional<ApproverLogDto> findLastLogForModelApprover(Long id) {
        Optional<ApproverLog> approverLog = getRepository().findLastApproverLogOfModelAprover(id);
        if (approverLog.isPresent()) return Optional.of(mapToDto(approverLog.get()));
        return Optional.empty();
    }

    @Override
    public SearchDto findAllByModelVersionId(long modelVersionId, Pageable pageable) {
        Page<ApproverLog> approverLogs= this.getRepository().findAllByModelVersionId(modelVersionId,pageable);
        List<ApproverLogDto> content = approverLogs.map(this::mapToDto).getContent();


        return new SearchDto.Builder().content(content)
                .totalElements(approverLogs.getTotalElements())
                .totalPages(approverLogs.getTotalPages())
                .currentPage(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    private ApproverLogDto mapToDto(ApproverLog approverLog) {
        ApproverLogDto approverLogDto = new ApproverLogDto();
        approverLogDto.setApproveStateCode(approverLog.getApproveState().getCode());
        approverLogDto.setApproveStateTitle(approverLog.getApproveState().getFaName());
        approverLogDto.setId(approverLog.getId());
        approverLogDto.setModelApproverId(approverLog.getModelApprover().getId());
        approverLogDto.setStateDate(approverLog.getStateDate());
        return approverLogDto;
    }


}