package com.hamsoo.bpm;


import com.hamsoo.bpm.common.utils.wrapper.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
//@EnableProcessApplication("hamsoo-bpm")

@RestController
@RequestMapping("/api/v1")
public class BpmApplication {
	@Value("${spring.profiles.active}")
	private String profile;
	@Value("${spring.datasource.url}")
	private String dbUrl;


	@Value("${spring.mail.username}")
	private String fromAddress;

	@Value("${hamsoo.mail.templates.path}")
	private String templatePath;

	@Value("${hamsoo.sinup.mail.template.name}")
	private String templateName;


	@Value("${hamsoo.confirmEmail.url}")
	private String confirmEmailUrl;

	@Value("${hamsoo.logo.url}")
	private String hamsooLogoUrl;

	@Value("${hamsoo.mail.verify.url}")
	private String mailVerifyUrl;

	private final MailService mailService;

	public BpmApplication(MailService mailService) {
		this.mailService = mailService;
	}

	public static void main(String[] args)  {



		SpringApplication.run(BpmApplication.class, args);





	}

	@PostConstruct
	void test() throws MessagingException, UnsupportedEncodingException {
		System.out.println("test ***"+profile);
		System.out.println("test ***"+dbUrl);

	}
	 @GetMapping("/public/mail-send/{mail}")
	 @PreAuthorize("permitAll()")
	 public void sendMail(@PathVariable(value = "mail") String mail){

		Map<String, Object> params = new HashMap<>();
		params.put("name", "test");
		params.put("confirmEmail", confirmEmailUrl);
		params.put("hamsooLogo", hamsooLogoUrl);
		params.put("url", mailVerifyUrl + "?code=" + 123 + "&mail=" + mail);

		Map<String, ClassPathResource> inlinResources = new HashMap<>();
		ClassPathResource email = new ClassPathResource(confirmEmailUrl);
		if (email.exists())
			inlinResources.put("confirmEmail", email);

		ClassPathResource hamsoo = new ClassPathResource(hamsooLogoUrl);
		if (hamsoo.exists())
			inlinResources.put("hamsooLogo", hamsoo);

		try {
			mailService.send(fromAddress, mail, "test", "test",templatePath, templateName, params, inlinResources);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}










}
