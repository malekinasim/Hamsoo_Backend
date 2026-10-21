package com.hamsoo.bpm.service.api.iotype;

import com.hamsoo.bpm.model.dto.iotype.IoTypeDto;
import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.repository.jpa.iotype.IoTypeRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;

public interface IoTypeService extends BaseService<IoType, Long, IoTypeRepository> {

    IoType findByIdOrElseThrow(Long id);

    IoTypeDto mapToDto(IoType ioType);

    List<IoTypeDto> mapToDto(List<IoType> ioTypeList);

    List<IoTypeDto> getList();

}