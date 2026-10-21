package com.hamsoo.bpm.common.utils.wrapper;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class MailService {

    private JavaMailSender mailSender;


    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;

    }


    public void send(String fromAddress, String toAddress, String subject, String senderName, String content) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void send(String fromAddress, String toAddress, String subject, String senderName, String templatePath, String templateName,
                     Map<String, Object> params,
                     Map<String, ClassPathResource> inlineResource
    ) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        //MimeMessageHelper helper = new MimeMessageHelper(message);

        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                ctx.setVariable(key, params.get(key));
            }
        }

        SpringTemplateEngine htmlTemplateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templatePath);
        templateResolver.setSuffix(templateName);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(0);

        htmlTemplateEngine.setTemplateResolver(templateResolver);
        final String htmlContent = htmlTemplateEngine.process("", ctx);

        helper.setText(htmlContent, true);
        if (inlineResource != null && !inlineResource.isEmpty()) {
            for (String key : inlineResource.keySet()) {
                helper.addInline(key, inlineResource.get(key));
            }
        }
        mailSender.send(message);
    }
}
