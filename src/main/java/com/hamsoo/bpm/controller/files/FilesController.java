package com.hamsoo.bpm.controller.files;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.service.api.files.FilesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/files/xml/{id}")
    @PreAuthorize("hasAnyResource(@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> getXmlFileById(@PathVariable("id") Long id) {
        Files files = filesService.findByIdOrElseThrow(id);
        FilesDto filesDto = filesService.getXml(files);
        return new ResponseEntity<>(filesDto, HttpStatus.OK);
    }

    @GetMapping("/files/byte/download/{id}")
    public ResponseEntity<?> getByteFileById(@PathVariable("id") Long id) {
        Files files = filesService.findByIdOrElseThrow(id);
        if (!files.getFileType().equals(Files.FileType.BYTE)) {
            throw new BusinessException("error.files.fileTypeNotByte");
        }
        String contentDisposition = "attachment;filename=" + files.getName();
        return ResponseEntity.ok()
                .header("Content-Type", files.getMimeType())
                .header("Content-Disposition", contentDisposition)
                .header(HttpHeaders.CONTENT_LENGTH, files.getSize().toString())
                .body(files.getByteContent());
    }

    @GetMapping(value = "/files/byte/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getImageFile(@PathVariable("id") Long id) {
        Files files = filesService.findByIdOrElseThrow(id);
        FilesDto filesDto = filesService.getByteContent(files);
        return new ResponseEntity<>(filesDto, HttpStatus.OK);
    }

    @GetMapping(value = "/files/html/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHtmlFile(@PathVariable("id") Long id) {
        Files files = filesService.findByIdOrElseThrow(id);
        FilesDto filesDto = filesService.getByteContent(files);
        String s = new String(filesDto.getByteContent());
        filesDto.setXmlContent(s);
        return new ResponseEntity<>(filesDto, HttpStatus.OK);
    }


//    @GetMapping( value = "/public/get-file")
//    public ResponseEntity<?>  getFile() throws IOException {
//        InputStream in = getClass()
//                .getResourceAsStream("/round-4x.png");
//        Files files =new Files();
//        files.setName("logo");
//        byte[] bytes=IOUtils.toByteArray(in);
//        files.setByteContent(bytes);
//
//        files.setFileType(Files.FileType.BYTE);
//        files.setMimeType("image/png");
//        files.setSize(new Long(bytes.length));
//        files.setCreatedBy("test");
//        filesService.save(files);
//        return new ResponseEntity<>(HttpStatus.OK);
//
//    }

}
