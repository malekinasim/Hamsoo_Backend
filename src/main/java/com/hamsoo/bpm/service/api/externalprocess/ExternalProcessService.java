package com.hamsoo.bpm.service.api.externalprocess;

import com.hamsoo.bpm.model.dto.externalprocess.ExternalProcessDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.externalprocess.ExternalProcess;
import com.hamsoo.bpm.repository.jpa.externalprocess.ExternalProcessRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

public interface ExternalProcessService extends BaseService<ExternalProcess, Long, ExternalProcessRepository> {

    ExternalProcess findByIdOrElseThrow(Long id);

    ExternalProcess create(ExternalProcessDto externalProcessDto);

    ExternalProcess edit(ExternalProcessDto externalProcessDto);

    void passive(Long id);

    ExternalProcessDto mapToDto(ExternalProcess externalProcess);

    SearchDto list(ExternalProcessDto externalProcessDto, int page, int size, String orderBy, Sort.Direction direction);

}
