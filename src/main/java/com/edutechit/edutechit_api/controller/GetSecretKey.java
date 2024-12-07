package com.edutechit.edutechit_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
@RequestMapping("/api/get-secret-key")
public class GetSecretKey {
    // Generate secret key
    @GetMapping
    public ResponseEntity<String> getSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512"); //Tạo key generator với thuật toán HmacSHA512
            keyGen.init(512); // Khởi tạo key với độ dài 512 bits
            SecretKey secretKey = keyGen.generateKey(); // Tạo secret key
            return ResponseEntity.status(HttpStatus.OK).body(Base64.getEncoder().encodeToString(secretKey.getEncoded())); // Trả về secret key dưới dạng chuỗi
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }
}
