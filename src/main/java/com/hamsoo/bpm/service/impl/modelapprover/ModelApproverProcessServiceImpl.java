package com.hamsoo.bpm.service.impl.modelapprover;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.approverlog.ApproverLogDto;
import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.modelapprover.ModelApproverDto;
import com.hamsoo.bpm.model.dto.modelapprover.StartProcessResDTO;
import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.notificationaudience.NotificationAudienceDto;
import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.approverlog.ApproverLogService;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverProcessService;
import com.hamsoo.bpm.service.api.modelapprover.ModelApproverService;
import com.hamsoo.bpm.service.api.modelmember.ModelMemberService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.user.UserService;
import io.jsonwebtoken.lang.Assert;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ModelApproverProcessServiceImpl implements ModelApproverProcessService {
    private ModelApproverService modelApproverService;
    private ModelVersionService modelVersionService;
    private ApproverLogService approverLogService;
    private NotificationService notificationService;
    private ModelMemberService modelMemberService;
    private UserService userService;


    public ModelApproverProcessServiceImpl(ModelApproverService modelApproverService,
                                           ModelVersionService modelVersionService,
                                           ApproverLogService approverLogService,
                                           NotificationService notificationService,
                                           ModelMemberService modelMemberService,
                                           UserService userService) {
        this.modelApproverService = modelApproverService;
        this.modelVersionService = modelVersionService;
        this.approverLogService = approverLogService;
        this.notificationService = notificationService;
        this.modelMemberService = modelMemberService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StartProcessResDTO startProcess(Long modelVersionId, Boolean forceStart) {
        Assert.notNull(modelVersionId, "modelVersionId is null");
        List<ErrorDto> errorDtoList = modelVersionService.validateModelVersion(modelVersionId);
        if (CollectionUtils.isEmpty(errorDtoList) || (forceStart && !errorDtoList.stream().anyMatch(err -> err.getStatus().equals(ErrorDto.Status.ERROR)))) {
            //change modelVersion state to For Approve state
            ModelVersion modelVersion = modelVersionService.updateStatus(modelVersionId, ModelVersion.Status.FOR_APPROVE.getCode());
            //get NextModel Approve Or FYI notification
            List<ApproverLog> nextApproverLogs = new ArrayList<>();
            getProcessNextStep(nextApproverLogs, modelVersionId);
            if (nextApproverLogs.isEmpty() || !nextApproverLogs.stream().anyMatch(log -> log.getApproveState().getCode().equals(ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode()))) {
                //end of modelApprover list then Approve ModelVersion
                modelVersionService.updateStatus(modelVersion.getId(), ModelVersion.Status.APPROVE.getCode());

            }
            return new StartProcessResDTO(true, errorDtoList);
        }
        return new StartProcessResDTO(false, errorDtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelApproverDto approveModel(Long modelVersionId, ModelApproverDto modelApproverDto) {
        Assert.notNull(modelVersionId, "modelVersionId is null");
        Assert.notNull(modelApproverDto, "modelApproverDto is null");
        if (!modelApproverDto.getUserId().equals(SecurityUtils.getUserIdOfCurrentUserOrElseThrow()))
            throw new BusinessException("error.modelApprove.userCannotApproveOtherUserReference");

        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);

        Optional<ApproverLogDto> lastLog = approverLogService.findLastLogForModelApprover(modelApproverDto.getId());
        if (lastLog.isPresent() && lastLog.get().getApproveStateCode().equals(ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode())) {
            ApproverLogDto approverLogDto = new ApproverLogDto();
            approverLogDto.setModelApproverId(modelApproverDto.getId());
            approverLogDto.setStateDate(LocalDateTime.now());
            approverLogDto.setApproveStateCode(ApproverLog.ApproveLogState.APPROVE.getCode());

            ApproverLog approverLog = approverLogService.create(approverLogDto);
            if (approverLog == null) throw new BusinessException("error.modelApprove.approveError");

            //update modelApproverDto
            modelApproverDto = modelApproverService.edit(modelApproverDto);

            //get nextStepp
            List<ApproverLog> nextApproverLogs = new ArrayList<>();
            getProcessNextStep(nextApproverLogs, modelVersionId);
            if (nextApproverLogs.isEmpty() || !nextApproverLogs.stream().anyMatch(log -> log.getApproveState().getCode().equals(ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode()))) {
                //end of modelApprover list then Approve ModelVersion
                modelVersionService.updateStatus(modelVersion.getId(), ModelVersion.Status.APPROVE.getCode());

                //notify model approve to all modelMember
                String notificationText = ResourceBundleUtils.getMessage("notify.modelVersion.approve",
                        modelVersion.getModel().getName(),
                        modelVersion.getAsisTobe().getFaName(),
                        modelVersion.getModel().getCode()
                );

                this.createNotification(modelVersionId, notificationText);
            }
            return modelApproverDto;
        } else
            throw new BusinessException("error.modelApprove.incorrectAction", ApproverLog.ApproveLogState.APPROVE.name());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelApproverDto deferModel(Long modelVersionId, ModelApproverDto modelApproverDto) {
        Assert.notNull(modelVersionId, "modelVersionId is null");
        Assert.notNull(modelApproverDto, "modelApproverDto is null");

        if (!modelApproverDto.getUserId().equals(SecurityUtils.getUserIdOfCurrentUserOrElseThrow()))
            throw new BusinessException("error.modelApprove.userCannotDeferOtherUserReference");

        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(modelVersionId);
        Optional<ApproverLogDto> lastLog = approverLogService.findLastLogForModelApprover(modelApproverDto.getId());
        if (lastLog.isPresent() && lastLog.get().getApproveStateCode().equals(ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode())) {
            ApproverLogDto approverLogDto = new ApproverLogDto();
            approverLogDto.setModelApproverId(modelApproverDto.getId());
            approverLogDto.setStateDate(LocalDateTime.now());
            approverLogDto.setApproveStateCode(ApproverLog.ApproveLogState.REJECT.getCode());
            ApproverLog approverLog = approverLogService.create(approverLogDto);
            if (approverLog == null) throw new BusinessException("error.modelApprove.deferError");
            //update modelApproverDto
            modelApproverDto = modelApproverService.edit(modelApproverDto);
            //Reject ModelVersion
            modelVersionService.updateStatus(modelVersion.getId(), ModelVersion.Status.DEFFER.getCode());
            //passive modelApprover and approver logs
            modelApproverService.passiveAllNotCheckModelApprover(modelVersion.getId(), modelApproverDto.getId());

            //notify model reject to all modelMember
            String notificationText = ResourceBundleUtils.getMessage("notify.modelVersion.defer",
                    modelVersion.getModel().getName(),
                    modelVersion.getAsisTobe().getFaName(),
                    modelVersion.getModel().getCode()
            );


            this.createNotification(modelVersionId, notificationText);
            return modelApproverDto;
        } else
            throw new BusinessException("error.modelApprove.incorrectAction", ApproverLog.ApproveLogState.REJECT.name());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelApproverDto acknowledgeModel(Long modelVersionId, ModelApproverDto modelApproverDto) {
        //check model verson

        Assert.notNull(modelVersionId, "modelVersionId is null");
        Assert.notNull(modelApproverDto, "modelApproverDto is null");

        if (!modelApproverDto.getUserId().equals(SecurityUtils.getUserIdOfCurrentUserOrElseThrow()))
            throw new BusinessException("error.modelApprove.userCannotAcknowledgeOtherUserReference");

        modelVersionService.findByIdOrElseThrow(modelVersionId);
        Optional<ApproverLogDto> lastLog = approverLogService.findLastLogForModelApprover(modelApproverDto.getId());
        if (lastLog.isPresent() && lastLog.get().getApproveStateCode().equals(ApproverLog.ApproveLogState.FORYOURINFO.getCode())) {
            ApproverLogDto approverLogDto = new ApproverLogDto();
            approverLogDto.setModelApproverId(modelApproverDto.getId());
            approverLogDto.setStateDate(LocalDateTime.now());
            approverLogDto.setApproveStateCode(ApproverLog.ApproveLogState.ACKNOWLEDGE.getCode());
            ApproverLog approverLog = approverLogService.create(approverLogDto);
            if (approverLog == null) throw new BusinessException("error.modelApprove.deferError");
            //update modelApproverDto
            modelApproverDto = modelApproverService.edit(modelApproverDto);
            return modelApproverDto;
        } else
            throw new BusinessException("error.modelApprove.incorrectAction", ApproverLog.ApproveLogState.ACKNOWLEDGE.name());
    }


    private List<ApproverLog> getProcessNextStep(List<ApproverLog> approverLogs, Long modelVersionId) {

        Optional<ModelApprover> modelApprover = modelApproverService.getNextStepModelApprover(modelVersionId);
        if (modelApprover.isPresent()) {
            ApproverLogDto approverLogDto = new ApproverLogDto();
            approverLogDto.setModelApproverId(modelApprover.get().getId());
            approverLogDto.setStateDate(LocalDateTime.now());
            approverLogDto.setApproveStateCode(modelApprover.get().getLevel().getCode().equals(ModelApprover.ApproveLevelType.FYI.getCode()) ?
                    ApproverLog.ApproveLogState.FORYOURINFO.getCode() : ApproverLog.ApproveLogState.FORYOURAPPROVE.getCode());
            ApproverLog approverLog = approverLogService.create(approverLogDto);
            if (modelApprover.get().getLevel().getCode().equals(ModelApprover.ApproveLevelType.FYI.getCode())) {
                approverLogs.addAll(getProcessNextStep(approverLogs, modelVersionId));
            } else {
                approverLogs.add(approverLog);
                createNotifications(approverLog);
            }
        }
        return approverLogs;

    }

    private void createNotifications(ApproverLog approverLog) {
        String code = "notify.modelVersion.sendForApprove";
        if (approverLog.getApproveState().getCode().equals(ApproverLog.ApproveLogState.FORYOURINFO.getCode()))
            code = "notify.modelVersion.sendFYI";
        String notificationText = ResourceBundleUtils.getMessage(code,
                approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getModel().getName(),
                approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getAsisTobe().getFaName(),
                approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getModel().getCode()
        );

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotification(notificationText);
        notificationDto.setRefId(approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getId());
        notificationDto.setEntityType(Notification.EntityType.MODEL_VERSION.getValue());
        notificationDto.setNotificationApiAddressCode(Notification.ApiAddressCode.APPROVE_MODEL_API.getValue());
        List<NotificationAudienceDto> notificationAudienceDtoList = new ArrayList<>();

        ModelMember approverMember = approverLog.getModelApprover().getModelMemberRole().getModelMember();
        if (approverMember.getMemberType().equals(ModelMember.MemberType.USER.getCode())) {
            notificationAudienceDtoList.add(new NotificationAudienceDto(approverMember.getMemberId()));
        } else if (approverMember.getMemberType().equals(ModelMember.MemberType.POST.getCode())) {
            List<User> userList = userService.findAllPostMember(approverMember.getMemberId());
            notificationAudienceDtoList.addAll(userList.stream().map(user -> new NotificationAudienceDto(user.getId())).toList());
        } else if (approverMember.getMemberType().equals(ModelMember.MemberType.JOB.getCode())) {
            List<User> userList = userService.findAllJobMember(approverMember.getMemberId());
            notificationAudienceDtoList.addAll(userList.stream().map(user -> new NotificationAudienceDto(user.getId())).toList());
        } else if (approverMember.getMemberType().equals(ModelMember.MemberType.UNIT.getCode())) {
            List<User> userList = userService.findAllUnitMember(approverMember.getMemberId());
            notificationAudienceDtoList.addAll(userList.stream().map(user -> new NotificationAudienceDto(user.getId())).toList());
        }

        notificationDto.setNotificationAudienceDtoList(notificationAudienceDtoList);

        JSONObject notificationParamValues = new JSONObject();
        notificationParamValues.put("modelId", approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getModel().getId());
        notificationParamValues.put("model_version_state", approverLog.getModelApprover().getModelMemberRole().getModelMember().getModelVersion().getAsisTobe().getCode());
        notificationParamValues.put("openReferenceDrawer", "openReferenceDrawer");
        notificationDto.setNotificationParamValues(notificationParamValues);

        notificationService.validateAndCreate(notificationDto);
    }


    private void createNotification(Long modelVersionId, String notificationText) {
        List<User> members = modelMemberService.getMembersOfModel(modelVersionId);
        members.remove(SecurityUtils.getCurrentUserOrElseThrow());
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotification(notificationText);
        notificationDto.setRefId(modelVersionId);
        notificationDto.setEntityType(Notification.EntityType.MODEL_VERSION.getValue());
        notificationDto.setNotificationApiAddressCode(Notification.ApiAddressCode.APPROVE_MODEL_API.getValue());
        List<NotificationAudienceDto> notificationAudienceDtoList = members.stream()
                .map(user -> new NotificationAudienceDto(user.getId())).toList();
        notificationDto.setNotificationAudienceDtoList(notificationAudienceDtoList);

        JSONObject notificationParamValues = new JSONObject();
        ModelVersion modelVersion = modelVersionService.getById(modelVersionId);
        notificationParamValues.put("modelId", modelVersion.getModel().getId());
        notificationParamValues.put("model_version_state", modelVersion.getAsisTobe().getCode());
        notificationParamValues.put("openReferenceDrawer", "openReferenceDrawer");
        notificationDto.setNotificationParamValues(notificationParamValues);

        notificationService.validateAndCreate(notificationDto);


    }


}

