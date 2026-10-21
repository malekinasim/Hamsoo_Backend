package com.hamsoo.bpm.common.utils;

import com.hamsoo.bpm.model.dto.BpmnReport.BpmnReportDto;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.UserTaskImpl;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class BpmnHelperService {

    private BpmnModelInstance bpmnModelInstance;

    public BpmnHelperService(InputStream modelStream) {
        bpmnModelInstance = Bpmn.readModelFromStream(modelStream);
    }

    public BpmnHelperService(String xml) {
        bpmnModelInstance = Bpmn.readModelFromStream(IOUtils.toInputStream(xml, Charset.defaultCharset()));
    }

    public ModelElementInstance getElementById(String elementId) {

        return this.bpmnModelInstance.getModelElementById(elementId);
    }

    public boolean isSpecificElementByID(String elementId, Class<? extends ModelElementInstance> clazz) {

        Assert.notNull(elementId, "elementId is null");
        ModelElementInstance modelElementById = getElementById(elementId);
        return modelElementById != null && modelElementById.getClass().equals(clazz);
    }

    public List<Lane> getLanes() {
        return bpmnModelInstance.getModelElementsByType(Lane.class).stream().toList();
    }

    public List<UserTask> getUserTask() {
        return bpmnModelInstance.getModelElementsByType(UserTask.class).stream().toList();
    }

    public List<Gateway> getGateways() {
        return bpmnModelInstance.getModelElementsByType(Gateway.class).stream().toList();
    }


    /**
     *
     * get all gateway with previos userTask activities
     */
    public List<BpmnReportDto> getModelDecisions() {
      //  Assert.notNull(laneId, "laneId is null");
        List<Gateway> GatewaysList = getGateways();
        List<BpmnReportDto> bpmnReportDtoList = new ArrayList<>();

        List<FlowNode> gatewayFlowNodesList = null;
        for (Gateway gateway : GatewaysList) {

            gatewayFlowNodesList = gateway.getPreviousNodes().list();//.singleResult();

            for(FlowNode flowNodes : gatewayFlowNodesList) {
                List<FlowNode> flowNodesList = new ArrayList<>();

                if (flowNodes instanceof UserTask == true)
                {
                    flowNodesList.add(flowNodes);
                    BpmnReportDto bpmnReportDto = new BpmnReportDto();
                    bpmnReportDto.setGateWayName(gateway.getName());
                    // bpmnReportDto.setModelName(this.bpmnModelInstance.getModel().getModelName());
                    bpmnReportDto.setLaneName(getParentLaneId(flowNodes.getId(), "2"));
                    bpmnReportDtoList.add(bpmnReportDto);
                }
                else // while (flowNodes instanceof UserTask == false)
                    navigationFlowNodesList(flowNodes.getPreviousNodes().list(), bpmnReportDtoList, gateway);

            }
        }

        return bpmnReportDtoList;
    }

        private void navigationFlowNodesList(List<FlowNode> flowNodesList, List<BpmnReportDto> bpmnReportDtoList, Gateway gateway) {
        for (FlowNode fn : flowNodesList) {
            if (fn instanceof UserTask)
            {
                BpmnReportDto bpmnReportDto =new BpmnReportDto() ;
                bpmnReportDto.setGateWayName(gateway.getName());
                // bpmnReportDto.setModelName(this.bpmnModelInstance.getModel().getModelName());
                bpmnReportDto.setLaneName(getParentLaneId(fn.getId(), "2"));

                bpmnReportDtoList.add(bpmnReportDto);
            }
            else
                navigationFlowNodesList(fn.getPreviousNodes().list(), bpmnReportDtoList, gateway);

        }
    }
    public List<BpmnReportDto> getModelGatewaysWithLane() {
        //  Assert.notNull(laneId, "laneId is null");
        List<Gateway> GatewaysList = getGateways();
        List<BpmnReportDto> bpmnReportDtoList = new ArrayList<>();

        List<FlowNode> gatewayFlowNodesList = null;
        for (Gateway gateway : GatewaysList) {

            BpmnReportDto bpmnReportDto = new BpmnReportDto();
            bpmnReportDto.setGateWayName(gateway.getName());
            // bpmnReportDto.setModelName(this.bpmnModelInstance.getModel().getModelName());
            bpmnReportDto.setLaneName(getGatwayParentLaneId(gateway.getId(), "2"));

            bpmnReportDtoList.add(bpmnReportDto);
        }
        return bpmnReportDtoList;
    }

    public String getGatwayParentLaneId(String GatwayId,String type) {
        Assert.notNull(GatwayId, "GatwayId is null");
        String elementId = null;
        ModelElementInstance modelElementById = getElementById(GatwayId);

        if (modelElementById instanceof Gateway) {
            List<Lane> lanes = getLanes();

            Optional<Lane> parentLane = lanes.stream().filter(lane ->
                    lane.getFlowNodeRefs().stream().
                            anyMatch(flowNode -> flowNode.getId()
                                    .equals(((Gateway) modelElementById).getId()))).findFirst();

            if (parentLane.isPresent()) {
                if(type.equals("1"))
                    elementId = parentLane.get().getId();
                else
                    elementId = parentLane.get().getName();
            }

        }
        return elementId;
    }

    public List<BpmnReportDto> getUserActivities() {
        //  Assert.notNull(laneId, "laneId is null");
        List<Gateway> GatewaysList = getGateways();
        List<BpmnReportDto> bpmnReportDtoList = new ArrayList<>();
        BpmnReportDto bpmnReportDto ;
        for (Gateway gateway : GatewaysList) {
            bpmnReportDto = new BpmnReportDto();
            FlowNode flowNodes = gateway.getPreviousNodes().singleResult();

            CallActivity callActivity = null;
            while (flowNodes instanceof UserTask==false)
                flowNodes = flowNodes.getPreviousNodes().singleResult();

            if (flowNodes instanceof UserTask) {
                bpmnReportDto.setGateWayName(gateway.getName());
                bpmnReportDto.setModelName(this.bpmnModelInstance.getModel().getModelName());

                bpmnReportDto.setLaneName(getParentLaneId(flowNodes.getId(),"2"));

//                    List<Lane> lanes = getLanes();
//
//                   var EmStatistic = new Object() { long EMConectedCount = flowNodes.getId() ; };
//                    Optional<Lane> parentLane = lanes.stream().filter(lane ->
//                            lane.getFlowNodeRefs().stream().anyMatch(flowNode -> flowNode.getId()
//                                            .equals(flowNodes.getId())).findFirst());
//
//                    if (parentLane.isPresent())
//                        bpmnReportDto.setLaneName(parentLane.get().getId());
//
//
//                }

                bpmnReportDtoList.add(bpmnReportDto);
            }
        }
//            flowNodes.filterByType();
//            if (modelElementById instanceof Lane) {
//                Lane lane = (Lane) modelElementById;
//                return lane.getFlowNodeRefs().stream().filter(
//                        element -> element.getClass().equals(type)
//                ).map(BaseElement::getId).collect(Collectors.toList());
//            }
//        }
        return bpmnReportDtoList;
    }



    //return dircet parentLane of task by taskId
    public String getParentLaneId(String taskId,String type) {
        Assert.notNull(taskId, "taskId is null");
        String elementId = null;
        ModelElementInstance modelElementById = getElementById(taskId);

        if (modelElementById instanceof UserTask) {
            List<Lane> lanes = getLanes();

            Optional<Lane> parentLane = lanes.stream().filter(lane ->
                    lane.getFlowNodeRefs().stream().
                            anyMatch(flowNode -> flowNode.getId()
                                    .equals(((UserTask) modelElementById).getId()))).findFirst();

            if (parentLane.isPresent()) {
              if(type.equals("1"))
                  elementId = parentLane.get().getId();
              else
                  elementId = parentLane.get().getName();
            }

        }
        return elementId;
    }


    //return all element by specific type in lane
    public List<String> getLaneFlowNodesIdByType(String laneId, Class type) {
        Assert.notNull(laneId, "laneId is null");
        ModelElementInstance modelElementById = getElementById(laneId);

        if (modelElementById instanceof Lane) {
            Lane lane = (Lane) modelElementById;
            return lane.getFlowNodeRefs().stream().filter(
                    element -> element.getClass().equals(type)
            ).map(BaseElement::getId).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }


    //assign task to AssigneeDto List
    public String assignTask(String taskId, String users, String groups) {
        Assert.notNull(taskId, "taskId is null");
        Assert.notNull(users, "users is null");
        Assert.notNull(groups, "groups is null");

        ModelElementInstance modelElementById = this.bpmnModelInstance.getModelElementById(taskId);
        if (modelElementById instanceof UserTaskImpl) {
            UserTask userTask = (UserTask) modelElementById;
            userTask.setCamundaCandidateUsers(users);
            userTask.setCamundaCandidateGroups(groups);

        }
        return Bpmn.convertToString(this.bpmnModelInstance);

    }

    public String assignTaskToUsers(String taskId, String users) {
        Assert.notNull(taskId, "taskId is null");
        Assert.notNull(users, "users is null");

        ModelElementInstance modelElementById = this.bpmnModelInstance.getModelElementById(taskId);
        if (modelElementById instanceof UserTask) {
            UserTask userTask = (UserTask) modelElementById;
            userTask.setCamundaCandidateUsers(users);
        }
        return Bpmn.convertToString(this.bpmnModelInstance);

    }

    public String assignTaskToGroups(String taskId, String groups) {
        Assert.notNull(taskId, "taskId is null");
        Assert.notNull(groups, "groups is null");

        ModelElementInstance modelElementById = this.bpmnModelInstance.getModelElementById(taskId);
        if (modelElementById instanceof UserTask) {
            UserTask userTask = (UserTask) modelElementById;
            userTask.setCamundaCandidateUsers(groups);
        }
        return Bpmn.convertToString(this.bpmnModelInstance);

    }

}
