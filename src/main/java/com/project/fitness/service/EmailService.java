package com.project.fitness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	public void sendOtpByEmail(String email, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("khanumsaeeda56@gmail.com");
		message.setTo(email);
		message.setSubject("Password Reset OTP");
		message.setText("""
				Your otp to reset the password is %s.
				This is valid for 5 minutes.
				Do not share the otp with anyone
				""".formatted(otp));
		mailSender.send(message);
	}
}
