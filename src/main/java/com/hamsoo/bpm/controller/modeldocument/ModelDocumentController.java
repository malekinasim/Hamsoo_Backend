package com.hamsoo.bpm.controller.modeldocument;

import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.modeldocument.ModelDocumentDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument;
import com.hamsoo.bpm.service.api.files.FilesService;
import com.hamsoo.bpm.service.api.modeldocument.ModelDocumentService;
import com.hamsoo.bpm.validator.files.FileContentTypeValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ModelDocumentController {

    private final ModelDocumentService modelDocumentService;
    private final FilesService filesService;
    private final FileContentTypeValidator fileContentTypeValidator;

    public ModelDocumentController(ModelDocumentService modelDocumentService,
                                   FilesService filesService,
                                   FileContentTypeValidator fileContentTypeValidator) {
        this.modelDocumentService = modelDocumentService;
        this.filesService = filesService;
        this.fileContentTypeValidator = fileContentTypeValidator;
    }

    @GetMapping("/model-documents/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelDocumentId(#id,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> getModelDocumentById(@PathVariable("id") Long id) {
        ModelDocument modelDocument = modelDocumentService.findByIdOrElseThrow(id);
        ModelDocumentDto modelDocumentDto = modelDocumentService.mapToDto(modelDocument);
        return new ResponseEntity<>(modelDocumentDto, HttpStatus.OK);
    }

    /**
     * @param modelDocumentDto set request header content type -> multipart/form-data.
     *                         The important thing to note is that we've set the content type of
     *                         the data part (modelDocumentDto) as application/JSON.(swagger not support. postman support this.)
     *                         more info: https://www.baeldung.com/sprint-boot-multipart-requests
     */
    @PostMapping(value = "/model-documents", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelDocumentDto.modelVersionId,@resourceItemService.MODEL_DOCUMENT_EDIT)")
    public ResponseEntity<?> createModelDocument(@RequestPart("data") @Valid ModelDocumentDto modelDocumentDto,
                                                 @RequestPart("file") MultipartFile file) {
        FilesDto filesDto = filesService.map(file, Files.FileType.BYTE);
        fileContentTypeValidator.isUserValidType(filesDto.getMimeType());
        modelDocumentDto.setFilesDto(filesDto);
        ModelDocument modelDocument = modelDocumentService.create(modelDocumentDto);
        modelDocumentDto = modelDocumentService.mapToDto(modelDocument);
        return new ResponseEntity<>(modelDocumentDto, HttpStatus.OK);
    }

    @DeleteMapping("/model-documents/{id}")
    @PreAuthorize(value = "hasAnyResourceByModelDocumentId(#id,@resourceItemService.MODEL_DOCUMENT_EDIT)")
    public ResponseEntity<?> deleteModelDocument(@PathVariable Long id) {
        modelDocumentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/model-documents/modelVersion/{modelVersionId}/{elementId}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_DOCUMENT_EDIT)")
    public ResponseEntity<?> passiveAllModelVersionCommentOnElement(@PathVariable(value = "modelVersionId") Long modelVersionId, @PathVariable(value = "elementId") String elementId) {
        modelDocumentService.passiveByElementIdAndModelVersion(modelVersionId, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/model-documents/list/{modelVersionId:^[1-9]+[0-9]*$}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_CARD_INFO)")
    public ResponseEntity<?> list(@PathVariable Long modelVersionId,
                                  @RequestParam int page, @RequestParam int size,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction,
                                  @RequestBody ModelDocumentDto modelDocumentDto) {
        modelDocumentDto.setModelVersionId(modelVersionId);
        if (modelDocumentDto.getElementId() == null) modelDocumentDto.setElementId("null");
        SearchDto searchDto = modelDocumentService.list(modelDocumentDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/model-documents/elements/list/{modelVersionId:^[1-9]+[0-9]*$}")
    @PreAuthorize(value = "hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> AllElementDocumentList(@PathVariable Long modelVersionId,
                                                    @RequestBody ModelDocumentDto modelDocumentDto,
                                                    @RequestParam(required = false) String orderBy,
                                                    @RequestParam(required = false) Sort.Direction direction) {
        modelDocumentDto.setModelVersionId(modelVersionId);
        SearchDto searchDto = modelDocumentService.list(modelDocumentDto, 0, 200, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }


}
