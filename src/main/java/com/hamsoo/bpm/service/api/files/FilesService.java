package com.hamsoo.bpm.service.api.files;

import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.repository.jpa.files.FilesRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.web.multipart.MultipartFile;

public interface FilesService extends BaseService<Files, Long, FilesRepository> {

    Files findByIdOrElseThrow(Long id);

    Files create(FilesDto filesDto);

    Files edit(FilesDto filesDto);

    FilesDto map(MultipartFile multipartFile, Files.FileType fileType);

    FilesDto map(Files files);

    FilesDto mapToDto(Files files);

    FilesDto getXml(Files files);

    FilesDto getByteContent(Files files);

    void delete(Long id);
}
