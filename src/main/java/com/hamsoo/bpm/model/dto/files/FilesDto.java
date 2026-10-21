package com.hamsoo.bpm.model.dto.files;

import com.hamsoo.bpm.model.entity.files.Files;

public class FilesDto {

    private Long id;
    private Files.FileType fileType;
    private String xmlContent;
    private byte[] byteContent;
    private String mimeType;
    private Long size;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Files.FileType getFileType() {
        return fileType;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public FilesDto(Long id, Files.FileType fileType, String xmlContent, byte[] byteContent, String mimeType, Long size, String name) {
        this.id = id;
        this.fileType = fileType;
        this.xmlContent = xmlContent;
        this.byteContent = byteContent;
        this.mimeType = mimeType;
        this.size = size;
        this.name = name;
    }

    public static class Builder {
        private Long id;
        private Files.FileType fileType;
        private String xmlContent;
        private byte[] byteContent;
        private String mimeType;
        private Long size;
        private String name;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileType(Files.FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder xmlContent(String xmlContent) {
            this.xmlContent = xmlContent;
            return this;
        }

        public Builder byteContent(byte[] byteContent) {
            this.byteContent = byteContent;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        //size in Byte
        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public FilesDto build() {
            return new FilesDto(id, fileType, xmlContent, byteContent, mimeType, size, name);
        }
    }
}
