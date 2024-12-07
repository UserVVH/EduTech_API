package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.ChangePasswordDto;
import com.edutechit.edutechit_api.dto.ForgotPasswordDto;
import com.edutechit.edutechit_api.dto.LoginDto;
import com.edutechit.edutechit_api.dto.RegisterRequestDto;
import com.edutechit.edutechit_api.exception.AuthenticationFailedException;
import com.edutechit.edutechit_api.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Login
    @PostMapping("/login")
    public ResponseEntity<String> authenticationUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            String token = authService.login(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (AuthenticationFailedException e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Change password
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, @RequestHeader("Authorization") String token) {
        try {
            authService.changePassword(changePasswordDto, token);
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        } catch (Exception e) {
            log.error("Password change failed", e); // Ghi lại lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @ModelAttribute RegisterRequestDto registerRequestDto) {
        try {
            authService.register(registerRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            log.error("User registration failed", e); // Ghi lại lỗi nếu có
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        try {
            authService.forgotPassword(forgotPasswordDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body("Mật khẩu mới đã được gửi đến email");
        } catch (Exception e) {
            log.error("Forgot password process failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}