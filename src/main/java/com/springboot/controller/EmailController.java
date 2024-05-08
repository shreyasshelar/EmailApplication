package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;

import com.springboot.model.Email;
import com.springboot.model.EmailWithAttachment;
import com.springboot.service.EmailService;

@Controller
public class EmailController {

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@GetMapping("/mail")
	public String showMailForm() {
		return "mail";
	}

	@GetMapping("/mail-with-attachment")
	public String showMailFormWithAttachment() {
		return "mail-with-attachment";
	}

	@PostMapping("/send-email")
	public String sendHtmlEmail(@ModelAttribute("email") Email email, RedirectAttributes redirectAttributes) {
		Context context = new Context();
		context.setVariable("message", email.getBody());
		context.setVariable("subject", email.getSubject());
		try {
			emailService.sendEmailWithHtmlTemplate(email.getTo(), email.getSubject(), "email-template", context);
			return "redirect:/success";
		} catch (Exception e) {
			e.printStackTrace(); // Log the error
			String errorMessage = "Failed to send email: " + e.getMessage();
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			return "redirect:/error";
		}

	}

	@PostMapping("/send-email-with-attachment")
	public String sendEmailWithAttachment(
			@ModelAttribute("emailWithAttachment") EmailWithAttachment emailWithAttachment,
			RedirectAttributes redirectAttributes) {

		Context context = new Context();
		context.setVariable("message", emailWithAttachment.getBody());
		context.setVariable("subject", emailWithAttachment.getSubject());

		MultipartFile file = emailWithAttachment.getFile();

		try {
			emailService.sendEmailWithHtmlTemplateAndAttachment(emailWithAttachment.getTo(),
					emailWithAttachment.getSubject(), "email-template", context, file);

			return "redirect:/success";
		} catch (Exception e) {
			e.printStackTrace(); // Log the error
			String errorMessage = "Failed to send email: " + e.getMessage();
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			return "redirect:/error";
		}
	}

	@GetMapping("/success")
	public String showSuccessPage() {
		return "success"; // Return the success page
	}

	@GetMapping("/error")
	public String showErrorPage(Model model) {
		return "error"; // Return the error page
	}

}
