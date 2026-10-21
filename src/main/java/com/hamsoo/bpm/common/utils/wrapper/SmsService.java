package com.hamsoo.bpm.common.utils.wrapper;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Component
public class SmsService {

    @Value("${com.frame.username}")
    private String username;

    @Value("${com.frame.password}")
    private String password;

    @Value("${com.frame.sms.url}")
    private String smsUrl;

    @Value("${com.frame.login.url}")
    private String tokenUrl;

    public Boolean sendSMS(String[] numbers, String smsContent) {

        String token = getToken();
        if (StringUtils.hasText(token)) {
            JSONObject body = new JSONObject()
                    .put("Numbers", numbers)
                    .put("Message", smsContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> httpEntity = new HttpEntity(body.toString(), headers);
            ResponseEntity response = null;

            try {
                RestTemplate restTemplate = new RestTemplate();
                response = restTemplate.exchange(smsUrl, HttpMethod.POST, httpEntity, String.class, new Object[0]);
            } catch (RestClientException var7) {
                return false;

            }
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        }
        return false;
    }

    public Boolean sendSMS(String[] numbers, Map<String, String> params, String templateUri) {
        String smsContent = getSmsString(params, templateUri);
        Stream.of(numbers).forEach(mobile -> "98".concat(mobile));
        String token = getToken();
        if (StringUtils.hasText(token)) {
            JSONObject body = new JSONObject()
                    .put("Numbers", numbers)
                    .put("Message", smsContent);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.setBearerAuth(token);
            HttpEntity<?> httpEntity = new HttpEntity(body.toString(), headers);
            ResponseEntity response = null;

            try {
                RestTemplate restTemplate = new RestTemplate();
                response = restTemplate.exchange(smsUrl, HttpMethod.POST, httpEntity, String.class, new Object[0]);
            } catch (RestClientException var7) {
                return false;

            }
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        }
        return false;
    }

    private String getToken() {
        JSONObject body = new JSONObject()
                .put("username", username)
                .put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<?> httpEntity = new HttpEntity(body.toString(), headers);
        ResponseEntity response = null;

        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(tokenUrl, HttpMethod.POST, httpEntity, String.class, new Object[0]);
        } catch (RestClientException var7) {
            return null;

        }
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            JSONObject obj = new JSONObject(response.getBody().toString());
            if (obj.has("token") && obj.get("token") != null)
                return obj.get("token").toString();
        }
        return null;

    }

    public String getSmsString(Map params, String templateUrl) {
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(props);

        Template t = ve.getTemplate(templateUrl);
        VelocityContext context = new VelocityContext(params);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }
}
