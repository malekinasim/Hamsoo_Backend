package com.hamsoo.bpm.service.api.log;

import com.hamsoo.bpm.model.dto.log.LogFileDto;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface LogService {

    List<File> getLogFiles(int page, int size);

    Optional<File> getLogFile(String name);

    LogFileDto mapToDto(File file);

    List<LogFileDto> mapToDto(List<File> fileList);

}
