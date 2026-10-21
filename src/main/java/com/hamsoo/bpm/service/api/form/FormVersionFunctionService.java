package com.hamsoo.bpm.service.api.form;

import com.hamsoo.bpm.model.dto.form.FunctionDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.form.Function;
import org.springframework.data.domain.Sort;

public interface FormVersionFunctionService {

    Function findByIdOrElseThrow(Long functionId, Long formVersionId);

    Function addFunction(FunctionDto functionDto);

    Function editFunction(FunctionDto functionDto);

    void passiveFunction(Long functionId, Long formVersionId);

    SearchDto list(FunctionDto functionDto, int page, int size, String orderBy, Sort.Direction direction,
                   java.util.function.Function<Function, FunctionDto> mapper);

    void copyFormVersionFunctionsIfExist(Long formVersionIdFrom, Long formVersionIdTo);
}
