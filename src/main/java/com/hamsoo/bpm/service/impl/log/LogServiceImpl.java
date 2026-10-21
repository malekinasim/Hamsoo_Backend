package com.hamsoo.bpm.service.impl.log;

import com.hamsoo.bpm.common.exception.ServiceException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.log.LogFileDto;
import com.hamsoo.bpm.service.api.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LogServiceImpl implements LogService {

    @Value("${logging.directory}")
    private String loggingDirectory;

    @Override
    public List<File> getLogFiles(int page, int size) {
        if (size > PageUtils.MAX_PAGE_SIZE) {
            size = PageUtils.MAX_PAGE_SIZE;
        }
        List<File> fileList;
        try (Stream<Path> filePathStream = Files.walk(Paths.get(loggingDirectory))) {
            fileList = filePathStream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .skip(page).limit(size).toList();
        } catch (IOException e) {
            throw new ServiceException("error.log.errorInReadLogFiles");
        }
        return fileList;
    }

    @Override
    public Optional<File> getLogFile(String name) {
        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }
        Optional<File> optionalFile;
        try (Stream<Path> filePathStream = Files.walk(Paths.get(loggingDirectory))) {
            optionalFile = filePathStream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> StringUtils.hasText(file.getName()) && file.getName().equals(name))
                    .findFirst();
        } catch (IOException e) {
            throw new ServiceException("error.log.errorInReadLogFiles");
        }
        return optionalFile;
    }

    @Override
    public LogFileDto mapToDto(File file) {
        LogFileDto logFileDto = new LogFileDto();
        logFileDto.setName(file.getName());
        logFileDto.setSize(file.length());
        LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()),
                ZoneId.systemDefault());
        logFileDto.setLastModified(lastModified);
        return logFileDto;
    }

    @Override
    public List<LogFileDto> mapToDto(List<File> fileList) {
        if (CollectionUtils.isEmpty(fileList)) {
            return new ArrayList<>();
        }
        return fileList.stream().map(this::mapToDto).toList();
    }

}
