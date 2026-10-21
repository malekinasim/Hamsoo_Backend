package com.hamsoo.bpm.service.impl.files;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.exception.ServiceException;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.repository.jpa.files.FilesRepository;
import com.hamsoo.bpm.service.api.files.FilesService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class FilesServiceImpl implements FilesService {

    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private final FilesRepository filesRepository;

    public FilesServiceImpl(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    @Override
    public FilesRepository getRepository() {
        return filesRepository;
    }

    @Override
    public Files findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.files.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.files.idIsNotValid"));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Files create(FilesDto filesDto) {
        Assert.notNull(filesDto, "filesDto is null");
        Files files = new Files();
        files.setFileType(filesDto.getFileType());
        files.setXmlContent(filesDto.getXmlContent());
        files.setByteContent(filesDto.getByteContent());
        files.setMimeType(filesDto.getMimeType());
        files.setSize(filesDto.getSize());
        files.setName(filesDto.getName());
        return save(files);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Files edit(FilesDto filesDto) {
        Assert.notNull(filesDto, "filesDto is null");
        Files files = findByIdOrElseThrow(filesDto.getId());
        files.setFileType(filesDto.getFileType());
        files.setXmlContent(filesDto.getXmlContent());
        files.setByteContent(filesDto.getByteContent());
        files.setMimeType(filesDto.getMimeType());
        files.setSize(filesDto.getSize());
        files.setName(filesDto.getName());
        return save(files);
    }

    /**
     * return value used as input for {@link #create(FilesDto)}
     */
    @Override
    public FilesDto map(MultipartFile multipartFile, Files.FileType fileType) {
        Assert.notNull(fileType, "fileType is null");
        if (multipartFile == null) {
            throw new ServiceException("error.files.fileIsNull");
        }
        FilesDto.Builder builder = new FilesDto.Builder();
        try {
            if (Files.FileType.XML == fileType) {
                String xml = IOUtils.toString(multipartFile.getInputStream(), StandardCharsets.UTF_8);
                builder.xmlContent(xml);
                builder.fileType(Files.FileType.XML);
            } else {
                builder.byteContent(multipartFile.getBytes());
                builder.fileType(Files.FileType.BYTE);
            }
        } catch (IOException e) {
            throw new ServiceException("error.files.errorInReadingOrClosingInputStream");
        }
        builder.mimeType(multipartFile.getContentType())
                .name(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize());
        return builder.build();
    }

    /**
     * return value used as input for {@link #create(FilesDto)}
     */
    @Override
    public FilesDto map(Files files) {
        Assert.notNull(files, "files is null");
        return new FilesDto.Builder()
                .fileType(files.getFileType())
                .xmlContent(files.getXmlContent())
                .byteContent(files.getByteContent())
                .mimeType(files.getMimeType())
                .size(files.getSize())
                .name(files.getName())
                .build();
    }

    /**
     * return file entity data without content
     */
    @Override
    public FilesDto mapToDto(Files files) {
        Assert.notNull(files, "files is null");
        return new FilesDto.Builder()
                .id(files.getId())
                .fileType(files.getFileType())
                .mimeType(files.getMimeType())
                .size(files.getSize())
                .name(files.getName())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FilesDto getXml(Files files) {
        Assert.notNull(files, "files is null");
        if (files.getFileType() != Files.FileType.XML) {
            throw new BusinessException("error.files.fileTypeNotXml");
        }
        files = findByIdOrElseThrow(files.getId());
        return new FilesDto.Builder()
                .id(files.getId())
                .fileType(files.getFileType())
                .xmlContent(XML_DECLARATION + files.getXmlContent())
                .mimeType(files.getMimeType())
                .size(files.getSize())
                .name(files.getName())
                .build();
    }

    @Override
    public FilesDto getByteContent(Files files) {
        Assert.notNull(files, "files is null");
        if (files.getFileType() != Files.FileType.BYTE) {
            throw new BusinessException("error.files.fileTypeNotXml");
        }
        files = findByIdOrElseThrow(files.getId());
        return new FilesDto.Builder()
                .id(files.getId())
                .fileType(files.getFileType())
                .byteContent(files.getByteContent())
                .mimeType(files.getMimeType())
                .size(files.getSize())
                .name(files.getName())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        Files files = findByIdOrElseThrow(id);
        delete(files);
    }

}
