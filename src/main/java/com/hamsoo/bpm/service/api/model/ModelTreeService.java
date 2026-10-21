package com.hamsoo.bpm.service.api.model;

import com.hamsoo.bpm.model.entity.model.ModelTree;
import com.hamsoo.bpm.service.impl.model.ModelTreeServiceImpl;
import com.hamsoo.bpm.repository.jpa.model.ModelTreeRepository;
import com.hamsoo.bpm.service.api.base.ReadOnlyBaseService;

import java.util.List;


public interface ModelTreeService extends ReadOnlyBaseService<ModelTree, Long, ModelTreeRepository> {
    List<ModelTree> getCurrentOrganTree(String asisTobeCode);
    ModelTreeServiceImpl.ModelStatisticsDto getModelStatistic(String asisTobeCode);

}