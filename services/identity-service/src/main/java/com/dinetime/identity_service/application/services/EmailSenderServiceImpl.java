package com.dinetime.identity_service.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dinetime.ports.output.EmailSenderService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String code, String token) {
        String subject = "Your DineTime Verification Code";

        String verifyLink = "https://yourdomain.com/verify-email?token=" + token;

        String htmlContent = """
                <p>Hi there 👋,</p>
                <p>You're almost in! Use the code below to verify your email address:</p>

                <h2 style="color: #333;">%s</h2>

                <p>Or just click the button below to verify automatically:</p>
                <p>
                    <a href="%s" style="
                        display: inline-block;
                        padding: 12px 20px;
                        background-color: #ff5722;
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                    ">
                        Verify Email
                    </a>
                </p>

                <p>This code will expire in 10 minutes.</p>

                <p>If you didn't request this, you can safely ignore this email.</p>

                <p>— The DineTime Team 🍽️</p>
                """.formatted(code, verifyLink);

        String plainTextContent = String.format("""
                Hi there 👋,

                You're almost in! Use the code below to verify your email address:

                %s

                Or just open this link in your browser to verify automatically:

                %s

                This code will expire in 10 minutes.

                If you didn't request this, you can safely ignore this email.

                — The DineTime Team 🍽️
                """, code, verifyLink);

        sendMultipartEmail(to, subject, plainTextContent, htmlContent);
    }

    private void sendMultipartEmail(String to, String subject, String plainText, String html) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true = multipart

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, html); // plain text first, then html

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}