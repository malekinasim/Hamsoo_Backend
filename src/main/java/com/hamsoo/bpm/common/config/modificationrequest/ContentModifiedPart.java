package com.hamsoo.bpm.common.config.modificationrequest;


import com.hamsoo.bpm.common.utils.StringUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ContentModifiedPart implements Part {

    private final Part originalPart;
    private final String characterEncoding;

    public ContentModifiedPart(Part part, String characterEncoding) {
        this.originalPart = part;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String body = IOUtils.toString(originalPart.getInputStream(), characterEncoding);
        if (StringUtils.hasText(body)) {
            body = StringUtil.replaceSpecialCharacter(body);
        }
        return new ByteArrayInputStream(body.getBytes());
    }

    @Override
    public String getContentType() {
        return originalPart.getContentType();
    }

    @Override
    public String getName() {
        return originalPart.getName();
    }

    @Override
    public String getSubmittedFileName() {
        return originalPart.getSubmittedFileName();
    }

    @Override
    public long getSize() {
        return originalPart.getSize();
    }

    @Override
    public void write(String fileName) throws IOException {
        originalPart.write(fileName);
    }

    @Override
    public void delete() throws IOException {
        originalPart.delete();
    }

    @Override
    public String getHeader(String name) {
        return originalPart.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return originalPart.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return originalPart.getHeaderNames();
    }
}
