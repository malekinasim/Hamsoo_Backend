package com.hamsoo.bpm.model.entity.files;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "bpm_files", schema = "bpm", catalog = "BPM_DB")
public class Files extends AuditEntity<Long> {

    private Long id;
    private FileType fileType;
    private String xmlContent;
    private byte[] byteContent;
    private String mimeType;
    private Long size;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "file_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Column(name = "xml_content")
    @org.hibernate.annotations.Type(type = "com.hamsoo.bpm.model.basictype.SqlXmlType")
    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "byte_content")
    public byte[] getByteContent() {
        return byteContent;
    }

    public void setByteContent(byte[] byteContent) {
        this.byteContent = byteContent;
    }

    @Column(name = "mime_type", nullable = false)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(name = "size", nullable = false)
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum FileType {
        XML, BYTE
    }
}
