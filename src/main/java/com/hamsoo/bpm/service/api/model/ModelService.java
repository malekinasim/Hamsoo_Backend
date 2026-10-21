package com.hamsoo.bpm.service.api.model;

import com.hamsoo.bpm.model.dto.model.ModelDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.repository.jpa.model.ModelRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ModelService extends BaseService<Model, Long, ModelRepository> {
    Model findByIdOrElseThrow(Long id);

    Model create(ModelDto modelDto);

    Model updateCode(Long id, String code);

    void passive(Long id);

    Model rename(Long id, String name);

    Boolean toggleBookmarkState(Long id);

    ModelDto mapToDto(Model model);


    ModelDto mapToDtoWithLastVersionData(Model model);

    Optional<Model> findByCodeAndOrganId(String code, Long organId);

    Optional<Model> findByModelTypeCodeAndOrganId(String modelTypeCode, Long organId);

    SearchDto list(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction);

    Optional<Specification<Model>> createSpecification(ModelDto modelDto, boolean filterByMininVersion);

    SearchDto listModelDecisions(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction);

    SearchDto listSingleUserModels(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction);

    Integer countModelByOrganId(Long id);
}
