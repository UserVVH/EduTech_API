package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.repository.FollowRepository;
import com.edutechit.edutechit_api.service.email.EmailService;
import com.edutechit.edutechit_api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class SubscribeController {

    @Autowired
    private UserService userService;

    @Autowired  // Thêm @Autowired để inject FollowRepository
    private FollowRepository followRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email, @RequestHeader("Authorization") String token) {
        try {
            userService.registerTracking(email, token);
            return ResponseEntity.status(HttpStatus.OK).body("Subscribed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String email) {
        emailService.unsubscribe(email);

        // Trả về trang HTML thông báo hủy đăng ký thành công
        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body("<!DOCTYPE html>" +
                        "<html lang='vi'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>Hủy Đăng Ký Thành Công</title>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        ".container { max-width: 600px; margin: 50px auto; padding: 20px; background-color: white; border-radius: 8px; text-align: center; }" +
                        "h1 { color: #4CAF50; }" +
                        "p { color: #555; }" +
                        ".button { margin-top: 20px; padding: 10px 20px; color: white; background-color: #4CAF50; border: none; border-radius: 5px; text-decoration: none; font-weight: bold; }" +
                        ".button:hover { background-color: #45a049; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<h1>Hủy Đăng Ký Thành Công</h1>" +
                        "<p>Bạn đã hủy đăng ký nhận email từ chúng tôi thành công.</p>" +
                        "<a href='http://localhost:3000' class='button'>Quay lại trang chủ</a>" +
                        "</div>" +
                        "</body>" +
                        "</html>");
    }

}
