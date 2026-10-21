package com.hamsoo.bpm.controller.log;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.log.LogFileDto;
import com.hamsoo.bpm.service.api.log.LogService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Hidden
@RestController
@RequestMapping("/api/v1")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs/list")
    public ResponseEntity<?> list(@RequestParam int page, @RequestParam int size) {
        List<File> logFiles = logService.getLogFiles(page, size);
        List<LogFileDto> logFileDtoList = logService.mapToDto(logFiles);
        return new ResponseEntity<>(logFileDtoList, HttpStatus.OK);
    }

    @GetMapping("/logs/{fileName}")
    public ResponseEntity<?> getLogFileByName(@PathVariable("fileName") String fileName) {
        Optional<File> optionalFile = logService.getLogFile(fileName);
        if (optionalFile.isEmpty()) {
            throw new BusinessException("error.log.fileNotFound");
        }
        File file = optionalFile.get();
        String contentType;
        byte[] bytes;
        try {
            Path path = file.toPath();
            contentType = Files.probeContentType(path);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new BusinessException("error.log.errorInReadLogFiles");
        }
        String contentDisposition = "attachment;filename=" + file.getName();
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", contentDisposition)
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(file.length()))
                .body(bytes);
    }

}
