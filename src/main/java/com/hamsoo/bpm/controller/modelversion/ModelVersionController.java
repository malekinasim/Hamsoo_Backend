package com.hamsoo.bpm.controller.modelversion;

import com.hamsoo.bpm.common.exception.ValidationException;
import com.hamsoo.bpm.common.utils.DateUtils;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.dto.modelversion.TeamWorkDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.modelversion.MilestoneService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import com.hamsoo.bpm.validator.files.FileContentTypeValidator;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;

import static com.hamsoo.bpm.controller.modelversion.TeamWorkController.TEAM_WORK_END_POINT;

@RestController
@RequestMapping("/api/v1")
public class ModelVersionController {

    private final ModelVersionService modelVersionService;
    private final FileContentTypeValidator fileContentTypeValidator;
    private final MilestoneService milestoneService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ModelVersionController(ModelVersionService modelVersionService,
                                  FileContentTypeValidator fileContentTypeValidator,
                                  MilestoneService milestoneService,
                                  SimpMessagingTemplate simpMessagingTemplate) {
        this.modelVersionService = modelVersionService;
        this.fileContentTypeValidator = fileContentTypeValidator;
        this.milestoneService = milestoneService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/model-versions/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> getModelVersionById(@PathVariable("id") Long id) {
        ModelVersion modelVersion = modelVersionService.findByIdOrElseThrow(id);
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PostMapping("/model-versions/updateXml/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_EDIT)")
    public ResponseEntity<?> updateXml(@RequestParam("file") MultipartFile file,
                                       @PathVariable("id") Long id,
                                       @RequestParam(required = false) boolean sendNotification) {
        Long fileId = modelVersionService.updateXml(file, id);
        if (sendNotification) {
            sendNotification(id);
        }
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    @PostMapping("/model-versions/update-xml-img-and-send-notification/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_EDIT)")
    public ResponseEntity<?> updateXmlAndImageAndSendNotification(@RequestParam("xml") MultipartFile xml,
                                                                  @RequestParam("image") MultipartFile image,
                                                                  @PathVariable("id") Long id,
                                                                  @RequestParam(required = false) boolean disableSendNotification) {
        fileContentTypeValidator.isImage(image.getContentType());
        ModelVersion modelVersion = modelVersionService.updateXmlAndImage(xml, image, id);
        if (!disableSendNotification) {
            sendNotification(id);
        }
        ModelVersionDto modelVersionDto = modelVersionService.mapToFilesData(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PostMapping("/model-versions/update-workflow/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_EDIT)")
    public ResponseEntity<?> updateWorkFlowAndSendNotification(@RequestParam("workFlow") MultipartFile workFlow,
                                                                  @PathVariable("id") Long id) {
        fileContentTypeValidator.isHtml(workFlow.getContentType());
        ModelVersion modelVersion = modelVersionService.updateWorkFlow(workFlow, id);

        ModelVersionDto modelVersionDto = modelVersionService.mapToFilesData(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    private void sendNotification(Long modelVersionId) {
        String fullName = SecurityUtils.getCurrentUserOrElseThrow().getFullName();
        String username = SecurityUtils.getCurrentAccountOrElseThrow().getUsername();
        String leftMessage = ResourceBundleUtils.getMessage("title.teamwork.updateModel", fullName);
        TeamWorkDto teamWorkDto = new TeamWorkDto(leftMessage, fullName, username, TeamWorkDto.MessageType.UPDATE_XML, modelVersionId.toString());
        simpMessagingTemplate.convertAndSend(MessageFormat.format(TEAM_WORK_END_POINT, modelVersionId.toString()), teamWorkDto);
    }

    @GetMapping("/model-versions/getXml/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_XML_FILE)")
    public ResponseEntity<?> getXml(@PathVariable("id") Long id) {
        FilesDto filesDto = modelVersionService.getXml(id);
        return new ResponseEntity<>(filesDto, HttpStatus.OK);
    }

    @PostMapping("/model-versions/updateImage/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_IMAGE_FILE)")
    public ResponseEntity<?> updateImage(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id) {
        fileContentTypeValidator.isImage(file.getContentType());
        Long fileId = null;
        try {
            fileId = modelVersionService.updateImage(file, id);
        } catch (ObjectOptimisticLockingFailureException e) {
            fileId = modelVersionService.updateImage(file, id);
        }
        if (fileId == null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    @GetMapping("/model-versions/getImage/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_IMAGE_FILE)")
    public ResponseEntity<?> getImage(@PathVariable("id") Long id) {
        FilesDto filesDto = modelVersionService.getImage(id);
        return new ResponseEntity<>(filesDto, HttpStatus.OK);
    }

    @GetMapping("/model-versions/sipoc-report/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MODEL_SIPOC_REPORT)")
    public ResponseEntity<?> report(@PathVariable("id") Long id) {
        byte[] content = modelVersionService.exportSipocReport(id);
        String currentDateTime = DateUtils.currentDateTimePersianInString("yyyyMMdd_HHmmss", "en");
        String outputFileName = String.format("\"sipoc_A4_%s.pdf\"", currentDateTime);
        String contentDisposition = "attachment;filename=" + outputFileName;
        return ResponseEntity.ok()
                .header("Content-Type", MediaType.APPLICATION_PDF_VALUE)
                .header("Content-Disposition", contentDisposition)
                .body(content);
    }

    @PutMapping(value = "/model-versions/rename-milestone-name/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MILESTONES_MNG)")
    public ResponseEntity<?> renameMilestoneName(@PathVariable("id") Long id,
                                                 @RequestBody @Validated(ModelVersionDto.Milestone.class) ModelVersionDto modelVersionDto,
                                                 @Parameter(hidden = true) Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        ModelVersion modelVersion = modelVersionService.renameMilestoneName(id, modelVersionDto);
        modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PutMapping(value = "/model-versions/milestone/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.MILESTONES_MNG)")
    public ResponseEntity<?> milestone(@PathVariable("id") Long id,
                                       @RequestBody @Validated(ModelVersionDto.Milestone.class) ModelVersionDto modelVersionDto,
                                       @Parameter(hidden = true) Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        ModelVersion modelVersion = milestoneService.milestone(id, modelVersionDto);
        modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PutMapping(value = "/model-versions/tobe-from-asis/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.ASIS_TOBE_VERSION_MNG)")
    public ResponseEntity<?> createTobeFromAsis(@PathVariable("id") Long id) {
        ModelVersion modelVersion = milestoneService.createTobeFromAsis(id);
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PutMapping(value = "/model-versions/operate-tobe-model/{id}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#id,@resourceItemService.ASIS_TOBE_VERSION_MNG)")
    public ResponseEntity<?> operateTobeModel(@PathVariable("id") Long id) {
        ModelVersion modelVersion = milestoneService.operateTobeModel(id);
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @PutMapping(value = "/model-versions/restore-to-old-version/{oldModelVersionId}")
    @PreAuthorize(value="hasAnyResourceByModelVersionId(#oldModelVersionId,@resourceItemService.MILESTONES_MNG)")
    public ResponseEntity<?> restoreToOldVersion(@PathVariable("oldModelVersionId") Long oldModelVersionId) {
        ModelVersion modelVersion = milestoneService.restoreToOldVersion(oldModelVersionId);
        ModelVersionDto modelVersionDto = modelVersionService.mapToDto(modelVersion);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }

    @GetMapping(value = "/model-versions/milestone-list/{modelId}")
    @PreAuthorize(value="hasAnyResourceByModelId(#modelId,@resourceItemService.MILESTONES_MNG)")
    public ResponseEntity<?> listAllModel(@PathVariable("modelId") Long modelId,
                                          @RequestParam ModelVersion.AsisTobe asisTobe,
                                          @RequestParam int page,
                                          @RequestParam int size,
                                          @RequestParam(required = false) String orderBy,
                                          @RequestParam(required = false) Sort.Direction direction) {
        ModelVersionDto modelVersionDto = new ModelVersionDto();
        modelVersionDto.setModelId(modelId);
        modelVersionDto.setAsisTobeCode(asisTobe.getCode());
        SearchDto searchDto = modelVersionService.milestoneList(modelVersionDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-versions/InValidModels")
    @PreAuthorize(value="hasAnyAuthority(@resourceItemService.MODEL_VALIDATION_REPORT)")
    public ResponseEntity<?> listInValidModel(@RequestBody ModelVersionDto modelDto,
                                                    @RequestParam int page, @RequestParam int size,
                                                    @RequestParam(required = false) String orderBy,
                                                    @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = modelVersionService.listInvalidModels(modelDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-versions/InValidModels/countReport")
    @PreAuthorize(value="hasAnyAuthority(@resourceItemService.MODEL_VALIDATION_REPORT)")
    public ResponseEntity<?> countInvalidModels(@RequestBody ModelVersionDto modelDto) {
        ModelVersionDto modelVersionDto  = modelVersionService.countInvalidModels(modelDto);
        return new ResponseEntity<>(modelVersionDto, HttpStatus.OK);
    }


}
