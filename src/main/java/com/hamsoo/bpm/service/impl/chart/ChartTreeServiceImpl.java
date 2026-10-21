package com.hamsoo.bpm.service.impl.chart;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.entity.chart.ChartTree;
import com.hamsoo.bpm.repository.jpa.chart.CharTreeRepository;
import com.hamsoo.bpm.service.api.chart.ChartTreeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartTreeServiceImpl implements ChartTreeService {
    private final CharTreeRepository charTreeRepository;

    public ChartTreeServiceImpl(CharTreeRepository charTreeRepository) {
        this.charTreeRepository = charTreeRepository;
    }

    @Override
    public CharTreeRepository getRepository() {
        return charTreeRepository;
    }

    @Override
    public List<ChartTree> getChartTree(ChartTree chartTree) {
        if (chartTree.getChartId() == null)
            throw new BusinessException("error.chart.idIsNotValid");

        List<ChartTree> chartTreeList;
        if (chartTree.getSearchWord() == null)
            chartTreeList = charTreeRepository.getChartTree(chartTree.getChartId(), SecurityUtils.getCurrentOrganIdOrElseThrow());
        else
            chartTreeList = charTreeRepository.getChartTree(chartTree.getChartId(),
                    chartTree.getSearchWord(),
                    SecurityUtils.getCurrentOrganIdOrElseThrow());

        List<ChartTree> result = getTreeByChildren(null, chartTreeList);
        List<ChartTree> finalChartTreeList = chartTreeList;
        chartTreeList.forEach(chartTree1 -> {
            if (chartTree1.getParentId() != null && finalChartTreeList.stream().noneMatch(chartTree2 -> chartTree1.getParentId().equals(chartTree2.getId()))) {
                result.addAll(getTreeByChildren(chartTree1.getParentId(), finalChartTreeList));
            }
        });

        return result;
    }

    private List<ChartTree> getTreeByChildren(String parentId, List<ChartTree> modelTreeList) {

        List<ChartTree> resutl = modelTreeList.stream().filter(chartTree -> {
            if (parentId == null) return chartTree.getParentId() == null;
            return chartTree.getParentId() != null && chartTree.getParentId().equals(parentId);

        }).collect(Collectors.toList());
        resutl.forEach(chartTree ->
                chartTree.setChildren(getTreeByChildren(chartTree.getId(), modelTreeList))
        );

        return resutl;
    }
}
