package com.edutechit.edutechit_api.service.email;

import com.edutechit.edutechit_api.repository.FollowRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EmailService {

    private final FollowRepository followRepository;
    private String host = "smtp.gmail.com"; // SMTP server
    private String port = "587"; // SMTP port (587 for TLS)
    private String username = "notification.eduvnua@gmail.com"; // Your email
    private String password = "thzr pqrw izim cpap"; // Your email password

    public EmailService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    //xử ly gui mail da luong
    public void sendEmail(String[] recipientEmails, String subject, String messageTemplate) {
        // Thiết lập các thuộc tính cho kết nối SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        // Tạo session với chứng thực username và password
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Sử dụng ExecutorService để quản lý luồng
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10 luồng cùng lúc

        for (String recipientEmail : recipientEmails) {
            executorService.submit(() -> {
                try {
                    // Tạo email message
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                    message.setSubject(subject);

                    String unsubscribeLink = "http://localhost:8080/api/unsubscribe?email=" + recipientEmail;
                    String htmlContent = "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<style>" +
                            "body {font-family: Arial, sans-serif; margin: 0; padding: 0;}" +
                            ".container {max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;}" +
                            ".header {background-color: #4CAF50; color: white; padding: 10px; text-align: center;}" +
                            ".content {background-color: white; padding: 20px; margin-top: 10px; border-radius: 8px;}" +
                            ".footer {text-align: center; padding: 10px; font-size: 12px; color: #999;}" +
                            "a {color: #4CAF50; text-decoration: none;}" +
                            ".button {display: inline-block; padding: 10px 20px; color: white; background-color: #4CAF50; border-radius: 5px; text-decoration: none; font-weight: bold;}" +
                            ".button:hover {background-color: #45a049;}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class='container'>" +
                            "<div class='header'>" +
                            "<h1>Tài liệu đã được xác nhận</h1>" +
                            "</div>" +
                            "<div class='content'>" +
                            "<p>Xin chào,</p>" +
                            "<p>Một tài liệu mới đã được xác thực và hiện tại có sẵn để bạn xem.</p>" +
                            "<a href='http://localhost:3000/' class='button'>Xem ngay</a>" +
                            "</div>" +
                            "<div class='footer'>" +
                            "<p>Nếu bạn không muốn nhận thêm email từ chúng tôi, vui lòng nhấp vào liên kết dưới đây để hủy đăng ký:</p>" +
                            "<a href='" + unsubscribeLink + "'>Hủy đăng ký</a>" +
                            "</div>" +
                            "</div></body></html>";

                    // Thiết lập nội dung email dưới dạng HTML
                    message.setContent(htmlContent, "text/html; charset=utf-8");

                    // Gửi email
                    Transport.send(message);
                    System.out.println("Email đã được gửi thành công đến: " + recipientEmail);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Lỗi gửi mail đến: " + recipientEmail + ": " + e.getMessage());
                }
            });
        }

        // Tắt ExecutorService sau khi tất cả các tác vụ đã hoàn thành
        executorService.shutdown();
    }

    @Transactional
    public void unsubscribe(String email) {
        followRepository.deleteByEmail(email);
    }

}
