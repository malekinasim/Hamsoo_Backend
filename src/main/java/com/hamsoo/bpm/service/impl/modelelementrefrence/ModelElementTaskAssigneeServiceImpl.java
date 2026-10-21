package com.hamsoo.bpm.service.impl.modelelementrefrence;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.BpmnHelperService;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.modelelementereference.AssigneeDto;
import com.hamsoo.bpm.model.dto.modelelementereference.ModelAssigneeDto;
import com.hamsoo.bpm.model.dto.modelelementereference.ModelElementReferenceDto;
import com.hamsoo.bpm.model.dto.unit.UnitDto;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.modelelementreference.ModelElementRefrence;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementReferenceService;
import com.hamsoo.bpm.service.api.modelelementreference.ModelElementTaskAssigneeService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.repository.jpa.modelelementrefrence.ModelElementRefrenceRepository;
import org.camunda.bpm.model.bpmn.impl.instance.UserTaskImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelElementTaskAssigneeServiceImpl implements ModelElementTaskAssigneeService {
    private final ModelElementRefrenceRepository modelElementRefrenceRepository;
    private final ModelElementReferenceService modelElementReferenceService;
    private final PostService postService;
    private final UnitService unitService;
    private final JobService jobService;
    private final UserService userService;
    private final ModelVersionService modelVersionService;
    private final FilesService filesService;

    public ModelElementTaskAssigneeServiceImpl(ModelElementRefrenceRepository modelElementRefrenceRepository, ModelElementReferenceService modelElementReferenceService, PostService postService, UnitService unitService, JobService jobService, UserService userService, ModelVersionService modelVersionService, FilesService filesService) {
        this.modelElementRefrenceRepository = modelElementRefrenceRepository;
        this.modelElementReferenceService = modelElementReferenceService;
        this.postService = postService;
        this.unitService = unitService;
        this.jobService = jobService;
        this.userService = userService;
        this.modelVersionService = modelVersionService;
        this.filesService = filesService;
    }

    @Override
    public ModelElementRefrenceRepository getRepository() {
        return modelElementRefrenceRepository;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelAssigneeDto create(ModelAssigneeDto modelTaskAssigneeDto) {
        ///assign task to user
        modelVersionService.checkModelVersionStatus(modelTaskAssigneeDto.getModelVersionId());
        FilesDto xmlFile = modelVersionService.getXml(modelTaskAssigneeDto.getModelVersionId());

        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());

        if (bpmnHelperService.isSpecificElementByID(modelTaskAssigneeDto.getElementId(), UserTaskImpl.class)) {
            String xml = assignToTask(xmlFile.getXmlContent(), modelTaskAssigneeDto);
            xmlFile.setXmlContent(xml);
            filesService.edit(xmlFile);


            List<ModelElementReferenceDto> modelElementRefrences =
                    mapToListOfModelElementReferenceDto(modelTaskAssigneeDto).stream().map(
                            modelElementReferenceDto -> {
                                ModelElementRefrence modelElementRefrence = modelElementReferenceService.create(modelElementReferenceDto);
                                return modelElementReferenceService.mapToDto(modelElementRefrence);
                            }
                    ).toList();

            //editLaneAssignee(xml, modelTaskAssigneeDto);
            return mapToModelTaskAssigneeDto(modelElementRefrences, bpmnHelperService);

        }
        throw new BusinessException("error.modelTaskAssigneeDto.elementIdIsNotTask");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelAssigneeDto edit(ModelAssigneeDto modelTaskAssigneeDto) {
        modelVersionService.checkModelVersionStatus(modelTaskAssigneeDto.getModelVersionId());
        FilesDto xmlFile = modelVersionService.getXml(modelTaskAssigneeDto.getModelVersionId());

        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());

        if (bpmnHelperService.isSpecificElementByID(modelTaskAssigneeDto.getElementId(), UserTaskImpl.class)) {
            String xml = assignToTask(xmlFile.getXmlContent(), modelTaskAssigneeDto);
            xmlFile.setXmlContent(xml);
            filesService.edit(xmlFile);


            List<ModelElementRefrence> modelElementReferences = modelElementRefrenceRepository
                    .findByElementIdAndModelVersionIdAndReferenceTypeList(modelTaskAssigneeDto.getElementId(),
                            modelTaskAssigneeDto.getModelVersionId(), SecurityUtils.getCurrentOrganIdOrElseThrow()
                            , List.of(ModelElementRefrence.ReferenceType.TASK_ASS_POST.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_UNIT.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_JOB.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_USER.getCode()));

            modelElementReferences.stream().filter(modelElementRefrence ->
                    !modelTaskAssigneeDto.getAssigneeDtos()
                            .stream().map(AssigneeDto::getModelElementReferenceId).toList().contains(
                                    modelElementRefrence.getId()
                            )).forEach(modelElementRefrence -> {
                modelElementReferenceService.delete(modelElementRefrence.getId());
            });


            List<ModelElementReferenceDto> modelElementRefrences =
                    mapToListOfModelElementReferenceDto(modelTaskAssigneeDto).stream()
                            .filter(modelElementReferenceDto -> modelElementReferenceDto.getId() == null)
                            .map(modelElementReferenceDto -> {
                                        ModelElementRefrence modelElementRefrence = modelElementReferenceService.create(modelElementReferenceDto);
                                        return modelElementReferenceService.mapToDto(modelElementRefrence);
                                    }
                            ).toList();


            return mapToModelTaskAssigneeDto(modelElementRefrences, bpmnHelperService);
        }
        throw new BusinessException("error.modelTaskAssigneeDto.elementIdIsNotTask");
    }

    private String assignToTask(String xml, ModelAssigneeDto modelTaskAssigneeDto) {

        String users = null;
        users = modelTaskAssigneeDto.getAssigneeDtos().stream()
                .filter(assigneeDto ->
                        assigneeDto.getType().equals(ModelElementRefrence.ReferenceType.TASK_ASS_USER.getCode()))
                .map(AssigneeDto::getCode).collect(Collectors.joining(","));

        String groups = null;
        groups = modelTaskAssigneeDto.getAssigneeDtos().stream()
                .filter(assigneeDto -> assigneeDto.getType().equals(ModelElementRefrence.ReferenceType.TASK_ASS_POST.getCode()) ||
                        assigneeDto.getType().equals(ModelElementRefrence.ReferenceType.TASK_ASS_UNIT.getCode()) ||
                        assigneeDto.getType().equals(ModelElementRefrence.ReferenceType.TASK_ASS_JOB.getCode()))
                .map(AssigneeDto::getCode).collect(Collectors.joining(","));

        if (users != null || groups != null) {
            BpmnHelperService bpmnHelperService = new BpmnHelperService(xml);

            if (users != null && groups != null)
                return bpmnHelperService.assignTask(modelTaskAssigneeDto.getElementId(), users, groups);
            else if (users != null)
                return bpmnHelperService.assignTaskToUsers(modelTaskAssigneeDto.getElementId(), users);
            else
                return bpmnHelperService.assignTaskToGroups(modelTaskAssigneeDto.getElementId(), groups);
        }
        return xml;
    }

    private List<ModelElementRefrence> getCurrenLaneAssignee(String laneId, Long modelVersionId) {
        List<ModelElementRefrence> currentLaneAssignees = modelElementRefrenceRepository
                .findByElementIdAndModelVersionIdAndReferenceTypeList(laneId,
                        modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow()
                        , List.of(ModelElementRefrence.ReferenceType.LANE_ASS_POST.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_UNIT.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_JOB.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_USER.getCode()));
        return currentLaneAssignees;
    }

    private List<ModelElementRefrence> getAllTaskOfLaneAssignee(String laneId, Long modelVersionId) {
        FilesDto xmlFile = modelVersionService.getXml(modelVersionId);
        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());
        List<String> elementIds = bpmnHelperService.getLaneFlowNodesIdByType(laneId, UserTaskImpl.class);

        if (!CollectionUtils.isEmpty(elementIds)) {
            List<ModelElementRefrence> currentLaneAllTaskAssignees = modelElementRefrenceRepository
                    .findByElementIdListAndModelVersionIdAndReferenceTypeList(elementIds.stream().toList(),
                            modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow()
                            , List.of(ModelElementRefrence.ReferenceType.TASK_ASS_POST.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_UNIT.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_JOB.getCode(),
                                    ModelElementRefrence.ReferenceType.TASK_ASS_USER.getCode()));
            return currentLaneAllTaskAssignees;
        }
        return new ArrayList<>();
    }

    private ModelElementReferenceDto mapTaskAssigneeToLaneAssignee(ModelElementReferenceDto modelElementReferenceDto, String laneId) {
        switch (ModelElementRefrence.ReferenceType.valueOf(modelElementReferenceDto.getReferenceTypeCode())) {
            case TASK_ASS_POST:
                modelElementReferenceDto.setReferenceTypeCode(ModelElementRefrence.ReferenceType.LANE_ASS_POST.getCode());
                break;
            case TASK_ASS_JOB:
                modelElementReferenceDto.setReferenceTypeCode(ModelElementRefrence.ReferenceType.LANE_ASS_JOB.getCode());
                break;
            case TASK_ASS_UNIT:
                modelElementReferenceDto.setReferenceTypeCode(ModelElementRefrence.ReferenceType.LANE_ASS_UNIT.getCode());
                break;
            case TASK_ASS_USER:
                modelElementReferenceDto.setReferenceTypeCode(ModelElementRefrence.ReferenceType.LANE_ASS_USER.getCode());
                break;
        }
        modelElementReferenceDto.setElementId(laneId);
        return modelElementReferenceDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editModelLanAssignee(String laneId, Long modelVersionId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelVersion.modelVersionIdIsNotLastVersion");
        }
        modelVersionService.checkModelVersionStatus(modelVersionId);
        List<ModelElementRefrence> currentLaneAssignees = getCurrenLaneAssignee(laneId, modelVersionId);

        List<ModelElementRefrence> currentAllTaskOfLaneAssignees = getAllTaskOfLaneAssignee(laneId, modelVersionId);

        List<ModelElementReferenceDto> laneAss = currentAllTaskOfLaneAssignees.stream().map(
                        modelElementReference -> {
                            ModelElementReferenceDto modelElementReferenceDto = modelElementReferenceService.mapToDto(modelElementReference);
                            return mapTaskAssigneeToLaneAssignee(modelElementReferenceDto, laneId);
                        })
                .toList();

        laneAss.stream().filter(modelElementReferenceDto ->
                currentLaneAssignees.stream().noneMatch(
                        modelElementRefrence ->
                                modelElementReferenceDto.getModelReferenceId().equals(modelElementRefrence.getReferenceId()) &&
                                        modelElementReferenceDto.getReferenceTypeCode().equals(modelElementRefrence.getReferenceType().getCode())

                )
        ).forEach(modelElementReferenceDto -> modelElementReferenceService.create(modelElementReferenceDto));


        currentLaneAssignees.stream().filter(modelElementRefrence ->
                laneAss.stream().noneMatch(
                        modelElementReferenceDto ->
                                modelElementReferenceDto.getModelReferenceId().equals(modelElementRefrence.getReferenceId()) &&
                                        modelElementReferenceDto.getReferenceTypeCode().equals(modelElementRefrence.getReferenceType().getCode())

                )
        ).forEach(modelElementRefrence -> modelElementReferenceService.delete(modelElementRefrence.getId()));


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editAllModelLaneAssignee(Long modelVersionId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelVersion.modelVersionIdIsNotLastVersion");
        }
        modelVersionService.checkModelVersionStatus(modelVersionId);
        FilesDto xmlFile = modelVersionService.getXml(modelVersionId);
        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());
        bpmnHelperService.getLanes().stream().forEach(lane -> this.editModelLanAssignee(lane.getId(), modelVersionId));
    }

    private List<ModelElementReferenceDto> mapToListOfModelElementReferenceDto(ModelAssigneeDto modelTaskAssigneeDto) {
        List<ModelElementReferenceDto> list = modelTaskAssigneeDto.getAssigneeDtos().stream().map(
                assigneeDto -> {
                    ModelElementReferenceDto modelElementReferenceDto = new ModelElementReferenceDto();
                    modelElementReferenceDto.setId(assigneeDto.getModelElementReferenceId());
                    modelElementReferenceDto.setModelVersionId(modelTaskAssigneeDto.getModelVersionId());
                    modelElementReferenceDto.setElementId(modelTaskAssigneeDto.getElementId());
                    modelElementReferenceDto.setModelReferenceId(assigneeDto.getId());
                    modelElementReferenceDto.setReferenceTypeCode(assigneeDto.getType());
                    return modelElementReferenceDto;
                }
        ).toList();
        return list;

    }

    private ModelAssigneeDto mapToModelTaskAssigneeDto(List<ModelElementReferenceDto> modelElementReferenceDtos,
                                                       BpmnHelperService bpmnHelperService) {
        ModelAssigneeDto modelTaskAssigneeDto = mapToModelTaskAssigneeDto(modelElementReferenceDtos);
        if (modelTaskAssigneeDto.getElementId() != null) {
            String laneId = bpmnHelperService.getParentLaneId(modelTaskAssigneeDto.getElementId(), "1");
            modelTaskAssigneeDto.setParentLaneId(laneId);
        }
        return modelTaskAssigneeDto;
    }

    private ModelAssigneeDto mapToModelTaskAssigneeDto(List<ModelElementReferenceDto> modelElementReferenceDtos) {
        if (!CollectionUtils.isEmpty(modelElementReferenceDtos)) {
            ModelAssigneeDto modelTaskAssigneeDto = new ModelAssigneeDto();

            modelTaskAssigneeDto.setModelVersionId(modelElementReferenceDtos.get(0).getModelVersionId());
            modelTaskAssigneeDto.setElementId(modelElementReferenceDtos.get(0).getElementId());
            modelTaskAssigneeDto.setAssigneeDtos(modelElementReferenceDtos.stream().map(
                    modelElementReferenceDto -> {
                        AssigneeDto assigneeDto = new AssigneeDto();
                        assigneeDto.setModelElementReferenceId(modelElementReferenceDto.getId());
                        assigneeDto.setId(modelElementReferenceDto.getModelReferenceId());
                        assigneeDto.setType(modelElementReferenceDto.getReferenceTypeCode());
                        assigneeDto = setModelAssigneeDetailInfo(assigneeDto);
                        return assigneeDto;
                    }
            ).toList());
            return modelTaskAssigneeDto;
        }
        return new ModelAssigneeDto();
    }

    private AssigneeDto setModelAssigneeDetailInfo(AssigneeDto assigneeDto) {

        switch (ModelElementRefrence.ReferenceType.valueOf(assigneeDto.getType())) {

            case TASK_ASS_POST, LANE_ASS_POST:
                Post post = postService.findByIdOrElseThrow(assigneeDto.getId());
                assigneeDto.setCode(post.getCode());
                assigneeDto.setName(post.getName());
                break;

            case TASK_ASS_JOB, LANE_ASS_JOB:
                Job job = jobService.findByIdOrElseThrow(assigneeDto.getId());
                assigneeDto.setCode(job.getCode());
                assigneeDto.setName(job.getName());
                break;

            case TASK_ASS_UNIT, LANE_ASS_UNIT:
                Unit unit = unitService.findByIdOrElseThrow(assigneeDto.getId());
                UnitDto unitDto = unitService.mapToDto(unit);
                assigneeDto.setCode(unitDto.getCode());
                assigneeDto.setName(unit.getName());
                break;
            case TASK_ASS_USER, LANE_ASS_USER:
                User user = userService.findByIdOrElseThrow(assigneeDto.getId());
                assigneeDto.setCode(user.getNationalCode());
                assigneeDto.setName(user.getFullName());
                break;

        }
        return assigneeDto;
    }

    @Override
    public void deleteAllModelElementAssigneeByModelVersionIdAndElementId(Long modelVersionId, String elementId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        if (!modelVersion.getLastVersion()) {
            throw new BusinessException("error.modelVersion.modelVersionIdIsNotLastVersion");
        }
        modelVersionService.checkModelVersionStatus(modelVersionId);

        List<ModelElementRefrence> modelElementRefrences = getRepository().
                findByElementIdAndModelVersionIdAndReferenceTypeList(elementId,
                        modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(),
                        List.of(ModelElementRefrence.ReferenceType.TASK_ASS_JOB.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_POST.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_USER.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_UNIT.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_JOB.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_POST.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_USER.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_UNIT.getCode())
                );
        modelElementRefrences.stream().forEach(ModelElementRefrence ->
                this.delete(ModelElementRefrence.getId())
        );
    }

    @Override
    public ModelAssigneeDto getElementTaskAssigneeListOfLastVersion(Long modelVersionId, String elementId) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);

        BpmnHelperService bpmnHelperService = new BpmnHelperService(modelVersion.getXml().getXmlContent());
        List<ModelElementRefrence> modelElementRefrences = getRepository().
                findByElementIdAndModelVersionIdAndReferenceTypeList(elementId,
                        modelVersionId, SecurityUtils.getCurrentOrganIdOrElseThrow(),
                        List.of(ModelElementRefrence.ReferenceType.TASK_ASS_JOB.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_POST.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_USER.getCode(),
                                ModelElementRefrence.ReferenceType.TASK_ASS_UNIT.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_JOB.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_POST.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_USER.getCode(),
                                ModelElementRefrence.ReferenceType.LANE_ASS_UNIT.getCode())
                );
        return mapToModelTaskAssigneeDto(modelElementRefrences.stream().map(
                modelElementRefrence ->
                        modelElementReferenceService.mapToDto(modelElementRefrence)).toList(), bpmnHelperService);
    }

    @Override
    public void delete(Long id) {
        ModelElementRefrence modelElementRefrence = modelElementReferenceService.findByIdOrElseThrow(id);
        modelVersionService.checkModelVersionStatus(modelElementRefrence.getModelVersion().getId());
        modelElementReferenceService.delete(modelElementRefrence.getId());
    }


}
