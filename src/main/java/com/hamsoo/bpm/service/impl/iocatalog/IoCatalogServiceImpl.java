package com.hamsoo.bpm.service.impl.iocatalog;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.iocatalog.IoCatalogDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog;
import com.hamsoo.bpm.model.entity.iocatalog.IoCatalog_;
import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.model.entity.iotype.IoType_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.iocatalog.IoCatalogRepository;
import com.hamsoo.bpm.service.api.identity.IdentityIoService;
import com.hamsoo.bpm.service.api.iocatalog.IoCatalogService;
import com.hamsoo.bpm.service.api.iotype.IoTypeService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IoCatalogServiceImpl implements IoCatalogService {

    private final IoCatalogRepository ioTypeRepository;
    private final IoTypeService ioTypeService;
    private final OrganService organService;
    private final IdentityIoService identityIoService;

    public IoCatalogServiceImpl(IoCatalogRepository ioTypeRepository,
                                IoTypeService ioTypeService,
                                OrganService organService,
                                IdentityIoService identityIoService) {
        this.ioTypeRepository = ioTypeRepository;
        this.ioTypeService = ioTypeService;
        this.organService = organService;
        this.identityIoService = identityIoService;
    }

    @Override
    public IoCatalogRepository getRepository() {
        return ioTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public IoCatalog findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.ioCatalog.idIsNotValid");
        }
        return getRepository()
                .findByIdAndStatusAndOrganId(id, Status.ACTIVE.name(), SecurityUtils.getCurrentOrganIdOrElseThrow())
                .orElseThrow(() -> new BusinessException("error.ioCatalog.idIsNotValid"));
    }

    @Override
    @Transactional(readOnly = true)
    public IoCatalogDto mapToDto(IoCatalog ioCatalog) {
        Assert.notNull(ioCatalog, "ioCatalog is null");
        ioCatalog = findByIdOrElseThrow(ioCatalog.getId());
        IoCatalogDto ioCatalogDto = new IoCatalogDto();
        ioCatalogDto.setId(ioCatalog.getId());
        ioCatalogDto.setName(ioCatalog.getName());
        ioCatalogDto.setIoTypeDto(ioTypeService.mapToDto(ioCatalog.getIoType()));
        ioCatalogDto.setOrganId(ioCatalog.getOrgan().getId());
        return ioCatalogDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IoCatalog create(IoCatalogDto ioCatalogDto) {
        Assert.notNull(ioCatalogDto, "ioCatalogDto is null");
        IoCatalog ioCatalog = new IoCatalog();
        ioCatalog.setName(ioCatalogDto.getName());
        ioCatalog.setStatus(Status.ACTIVE.name());
        ioCatalog.setIoType(ioTypeService.findByIdOrElseThrow(ioCatalogDto.getIoTypeId()));
        ioCatalog.setOrgan(organService.findByIdOrElseThrow(SecurityUtils.getCurrentOrganIdOrElseThrow()));
        return save(ioCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public IoCatalog edit(IoCatalogDto ioCatalogDto) {
        Assert.notNull(ioCatalogDto, "ioCatalogDto is null");
        IoCatalog ioCatalog = findByIdOrElseThrow(ioCatalogDto.getId());
        ioCatalog.setName(ioCatalogDto.getName());
        ioCatalog.setIoType(ioTypeService.findByIdOrElseThrow(ioCatalogDto.getIoTypeId()));
        return save(ioCatalog);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        IoCatalog ioCatalog = findByIdOrElseThrow(id);
        Long count = identityIoService.countByIoCatalogId(id);
        if (count > 0) {
            throw new BusinessException("error.ioCatalog.ioCatalogUsedInIdentityIo");
        }
        ioCatalog.setStatus(Status.PASSIVE.name());
        save(ioCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchDto list(IoCatalogDto ioCatalogDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<IoCatalog>> optionalSpecification = createSpecification(ioCatalogDto);
        Page<IoCatalog> modelPage = findAll(optionalSpecification.get(), pageable);
        List<IoCatalogDto> content = modelPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(modelPage.getTotalElements())
                .totalPages(modelPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<IoCatalog>> createSpecification(IoCatalogDto ioCatalogDto) {
        List<Specification<IoCatalog>> specificationList = new ArrayList<>();

        Specification<IoCatalog> statusEq = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(IoCatalog_.STATUS), Status.ACTIVE.name());
        specificationList.add(statusEq);

        Specification<IoCatalog> organEq = (root, query, criteriaBuilder) -> {
            Join<IoCatalog, Organ> organJoin = root.join(IoCatalog_.ORGAN);
            return criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
        };
        specificationList.add(organEq);

        if (ioCatalogDto.getName() != null) {
            Specification<IoCatalog> nameLike = (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(IoCatalog_.NAME), "%" + ioCatalogDto.getName() + "%");
            specificationList.add(nameLike);
        }

        if (ioCatalogDto.getIoTypeId() != null) {
            Specification<IoCatalog> ioTypeEq = (root, query, criteriaBuilder) -> {
                Join<IoCatalog, IoType> ioTypeJoin = root.join(IoCatalog_.IO_TYPE);
                return criteriaBuilder.equal(ioTypeJoin.get(IoType_.ID), ioCatalogDto.getIoTypeId());
            };
            specificationList.add(ioTypeEq);
        }
        return SpecificationUtils.andSpecifications(specificationList);
    }

}
