package com.hamsoo.bpm.service.impl.iotype;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.iotype.IoTypeDto;
import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.iotype.IoTypeRepository;
import com.hamsoo.bpm.service.api.iotype.IoTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IoTypeServiceImpl implements IoTypeService {

    private final IoTypeRepository ioTypeRepository;

    public IoTypeServiceImpl(IoTypeRepository ioTypeRepository) {
        this.ioTypeRepository = ioTypeRepository;
    }

    @Override
    public IoTypeRepository getRepository() {
        return ioTypeRepository;
    }

    @Override
    public IoType findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.ioType.idIsNotValid");
        }
        return getRepository().findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new BusinessException("error.ioType.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public IoTypeDto mapToDto(IoType ioType) {
        Assert.notNull(ioType, "ioType is null");
        ioType = findByIdOrElseThrow(ioType.getId());
        IoTypeDto ioTypeDto = new IoTypeDto();
        ioTypeDto.setId(ioType.getId());
        ioTypeDto.setName(ioType.getName());
        return ioTypeDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IoTypeDto> mapToDto(List<IoType> ioTypeList) {
        if (CollectionUtils.isEmpty(ioTypeList)) {
            return new ArrayList<>();
        }
        return ioTypeList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IoTypeDto> getList() {
        List<IoType> ioTypeList = getRepository().findByStatus(Status.ACTIVE.name(), PageUtils.getInstance());
        return mapToDto(ioTypeList);
    }

}