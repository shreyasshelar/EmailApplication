package com.springboot.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SpringTemplateEngine templateEngine;

	public void sendEmailWithHtmlTemplate(String to, String subject, String templateName, Context context) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			String htmlContent = templateEngine.process(templateName, context);
			helper.setText(htmlContent, true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMessage);
	}

	public void sendEmailWithHtmlTemplateAndAttachment(String to, String subject, String templateName, Context context,
			MultipartFile file) throws IOException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			String htmlContent = templateEngine.process(templateName, context);
			helper.setText(htmlContent, true);
			DataSource dataSource = new ByteArrayDataSource(file.getBytes(), file.getContentType());

			// Add the attachment using the DataSource
			helper.addAttachment(file.getOriginalFilename(), dataSource);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMessage);

	}

}
