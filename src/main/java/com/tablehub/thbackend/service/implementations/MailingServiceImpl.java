package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.service.interfaces.MailingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailingServiceImpl implements MailingService {

    private final JavaMailSender mailSender;

    private static final String FROM_EMAIL = "noreply@tablehub.local";
    private static final String RESET_PASSWORD_SUBJECT = "TableHub - Reset Your Password";
    private static final String DEEPLINK_FORMAT = "tablehub://reset-password?token=%s";

    private static final String RESET_PASSWORD_HTML_TEMPLATE = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333; text-align: center;">
            <div style="display: inline-block; text-align: center; max-width: 500px; width: 100%%;">
                <h2 style="color: #2C3E50;">Reset Your Password</h2>
                <p>Hello,</p>
                <p>To reset your password, please open the app using the link below:</p>
                <p>
                    <a href="%s" style="
                        display: inline-block;
                        padding: 12px 24px;
                        font-size: 16px;
                        color: #fff;
                        background-color: #3498db;
                        text-decoration: none;
                        border-radius: 5px;
                    ">Open App to Reset Password</a>
                </p>
                <p>If you did not request a password reset, please ignore this email.</p>
            </div>
        </body>
        </html>
        """;



    @Override
    public void sendResetPasswordEmail(String to, String token) {
        String deepLink = String.format(DEEPLINK_FORMAT, token);
        String htmlContent = String.format(RESET_PASSWORD_HTML_TEMPLATE, deepLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject(RESET_PASSWORD_SUBJECT);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset password email", e);
        }
    }
}
