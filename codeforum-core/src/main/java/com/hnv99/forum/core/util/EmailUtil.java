package com.hnv99.forum.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;

/**
 * Utility class for sending emails.
 */
@Slf4j
public class EmailUtil {
    /**
     * The email sender address.
     */
    private static volatile String from;

    /**
     * Gets the sender email address.
     *
     * If the sender address is not initialized, it retrieves it from the Spring configuration.
     *
     * @return The sender email address.
     */
    public static String getFrom() {
        if (from == null) {
            synchronized (EmailUtil.class) {
                if (from == null) {
                    from = SpringUtil.getConfig("spring.mail.from", "xhhuiblog@163.com");
                }
            }
        }
        return from;
    }

    /**
     * Sends an email using the Spring Boot email package.
     *
     * @param title The title of the email.
     * @param to The recipient email address.
     * @param content The content of the email.
     * @return true if the email is sent successfully, false otherwise.
     */
    public static boolean sendMail(String title, String to, String content) {
        try {
            JavaMailSender javaMailSender = SpringUtil.getBean(JavaMailSender.class);
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(getFrom());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(title);
            // Email content with support for HTML templates
            mimeMessageHelper.setText(content, true);
            // Fix for the "JavaMailSender no object DCH for MIME type multipart/mixed" issue
            Thread.currentThread().setContextClassLoader(EmailUtil.class.getClassLoader());
            javaMailSender.send(mimeMailMessage);
            return true;
        } catch (Exception e) {
            log.warn("sendEmail error {}@{}, {}", title, to, e);
            return false;
        }
    }
}

