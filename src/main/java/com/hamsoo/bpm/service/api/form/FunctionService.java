package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FunctionDto;
import com.hamsoo.bpm.model.entity.form.Function;
import com.hamsoo.bpm.repository.jpa.form.FunctionRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FunctionService extends BaseService<Function, Long, FunctionRepository> {

    Function create(FunctionDto FunctionDto);

    Function edit(FunctionDto functionDto);

    void passive(Long id);

    FunctionDto mapToDto(Function function);

    Optional<Function> findByIdAndFormVersionId(Long id, Long formVersionId);

    Page<Function> list(Long formVersionId, Pageable pageable);

    List<Function> findByFormVersionId(Long formVersionId);

}
