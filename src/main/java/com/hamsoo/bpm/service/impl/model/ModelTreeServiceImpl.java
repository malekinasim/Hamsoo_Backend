package com.hamsoo.bpm.service.impl.model;

import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.entity.model.ModelTree;
import com.hamsoo.bpm.repository.jpa.model.ModelTreeRepository;
import com.hamsoo.bpm.service.api.model.ModelTreeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ModelTreeServiceImpl implements ModelTreeService {
    private final ModelTreeRepository modelTreeRepository;

    public ModelTreeServiceImpl(ModelTreeRepository modelTreeRepository) {
        this.modelTreeRepository = modelTreeRepository;
    }

    @Override
    public ModelTreeRepository getRepository() {
        return modelTreeRepository;
    }

    @Override
    public List<ModelTree> getCurrentOrganTree(String asisTobeCode) {
        List<ModelTree> modelTreeList = this.modelTreeRepository.getCurrentOrganModelTree(SecurityUtils.getCurrentOrganIdOrElseThrow(), asisTobeCode);


        List<ModelTree> result = getTreeByChildren(null, modelTreeList);


        modelTreeList.forEach(modelTree -> {
            if (modelTree.getParentId() != null && modelTreeList.stream().noneMatch(modelTree1 -> modelTree.getParentId().equals(modelTree1.getId()))) {
                result.addAll(getTreeByChildren(modelTree.getParentId(), modelTreeList));
            }
        });

        return result;
    }

    private List<ModelTree> getTreeByChildren(Long parentId, List<ModelTree> modelTreeList) {

        List<ModelTree> resutl = modelTreeList.stream().filter(modelTree -> {
            if (parentId == null) return modelTree.getParentId() == null;
            return modelTree.getParentId() != null && modelTree.getParentId().equals(parentId);

        }).collect(Collectors.toList());
        resutl.forEach(modelTree ->
                modelTree.setChildren(getTreeByChildren(modelTree.getId(), modelTreeList))
        );

        return resutl;
    }

    public ModelStatisticsDto getModelStatistic(String asisTobeCode) {

        List<ModelTree> modelTreeList = this.modelTreeRepository.getCurrentOrganModelTree(SecurityUtils.getCurrentOrganIdOrElseThrow(), asisTobeCode);

//        long notConnected = modelTreeList.stream().filter(modelTree -> {
//            return modelTree.getParentId() == null && !modelTree.getTypeCode().equals("EM");
//        }).count();//.collect(Collectors.toList());

        long Connected = modelTreeList.stream().filter(modelTree -> (
             modelTree.getParentId() != null && !modelTree.getTypeCode().equals("EM")
        )).count();

        List<ModelTree> EmList = modelTreeList.stream().filter(modelTree -> {
            return modelTree.getParentId() == null && modelTree.getTypeCode().equals("EM");
        }).collect(Collectors.toList());

        var EmStatistic = new Object() { long EMConectedCount; };
        EmList.forEach(modelTree ->{
                           if(modelTreeList.stream().filter(modelTree1 -> (
                                           Objects.equals(modelTree1.getParentId(),modelTree.getId()))
                              // modelTree1.getParentId().toString().equals(modelTree.getId().toString()))//&& !modelTree.getTypeCode().equals("EM");
                           ).count()>0) EmStatistic.EMConectedCount++;
                }
        );

        long AllCount = modelTreeList.size();
        long ConnectedCount = EmStatistic.EMConectedCount+Connected;

        ModelStatisticsDto modelStatisticsDto =new ModelStatisticsDto();
        modelStatisticsDto.setAllCount(AllCount);
        modelStatisticsDto.setConnectedCount(ConnectedCount);
        modelStatisticsDto.setNotConnectedCount(AllCount-ConnectedCount);

        return modelStatisticsDto;
    }
    public class ModelStatisticsDto
    {
        private long allCount;
        private long connectedCount;
        private long notConnectedCount;

        public long getAllCount() {
            return allCount;
        }

        public void setAllCount(long allCount) {
            this.allCount = allCount;
        }

        public long getConnectedCount() {
            return connectedCount;
        }

        public void setConnectedCount(long connectedCount) {
            this.connectedCount = connectedCount;
        }

        public long getNotConnectedCount() {
            return notConnectedCount;
        }

        public void setNotConnectedCount(long notConnectedCount) {
            this.notConnectedCount = notConnectedCount;
        }
    }
}
