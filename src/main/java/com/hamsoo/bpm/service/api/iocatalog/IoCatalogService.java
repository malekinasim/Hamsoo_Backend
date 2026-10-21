package com.hamsoo.bpm.service.api.iocatalog;

import com.hamsoo.bpm.model.dto.iocatalog.IoCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.repository.jpa.iocatalog.IoCatalogRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

public interface IoCatalogService extends BaseService<IoCatalog, Long, IoCatalogRepository> {

    IoCatalog findByIdOrElseThrow(Long id);

    IoCatalogDto mapToDto(IoCatalog ioCatalog);

    IoCatalog create(IoCatalogDto ioCatalogDto);

    IoCatalog edit(IoCatalogDto ioCatalogDto);

    void passive(Long id);

    SearchDto list(IoCatalogDto ioCatalogDto, int page, int size, String orderBy, Sort.Direction direction);
}