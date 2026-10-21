package com.hamsoo.bpm.controller.model;

import com.hamsoo.bpm.model.dto.model.ModelManagementDto;
import com.hamsoo.bpm.service.api.model.ModelManagementService;
import com.hamsoo.bpm.validator.files.FileContentTypeValidator;
import com.hamsoo.bpm.validator.model.ModelManagementValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ModelManagementController {

    private final ModelManagementService modelManagementService;
    private final ModelManagementValidator modelManagementValidator;
    private final FileContentTypeValidator fileContentTypeValidator;

    public ModelManagementController(ModelManagementService modelManagementService,
                                     ModelManagementValidator modelManagementValidator,
                                     FileContentTypeValidator fileContentTypeValidator) {
        this.modelManagementService = modelManagementService;
        this.modelManagementValidator = modelManagementValidator;
        this.fileContentTypeValidator = fileContentTypeValidator;
    }

    @InitBinder("modelManagementDto")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(modelManagementValidator);
    }

    /**
     * @param modelManagementDto set request header content type -> multipart/form-data.
     *                           The important thing to note is that we've set the content type of
     *                           the data part (modelManagementDto) as application/JSON.(swagger not support. postman support this.)
     *                           more info: https://www.baeldung.com/sprint-boot-multipart-requests
     */
    @PostMapping(value = "/Model", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.MODEL_CREATE)")
    public ResponseEntity<?> createModel(@RequestPart("modelManagementDto") @Valid ModelManagementDto modelManagementDto,
                                         @RequestPart("xml") MultipartFile xml,
                                         @RequestPart("image") MultipartFile image) {
        fileContentTypeValidator.isImage(image.getContentType());
        modelManagementDto = modelManagementService.create(modelManagementDto, xml, image);
        return new ResponseEntity<>(modelManagementDto, HttpStatus.OK);
    }


//    @PutMapping(value = "/Model")
//   public ResponseEntity<?> updateModel( @RequestBody ModelManagementDto modelManagementDto) {
//        modelManagementDto = modelManagementService(modelManagementDto);
//        return new ResponseEntity<>(modelManagementDto, HttpStatus.OK);
//    }
}
