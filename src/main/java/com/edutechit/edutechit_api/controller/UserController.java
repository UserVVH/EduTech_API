package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.UpdateUserDto;
import com.edutechit.edutechit_api.dto.UserDocumentStatsDTO;
import com.edutechit.edutechit_api.dto.UserInfoDto;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Get the user info
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getMyUserInfo(@RequestHeader("Authorization") String token) {
        try {
            User user = userService.getUserInfoByToken(token);
            UserInfoDto userInfoDto = convertToUserInfoDto(user);
            return ResponseEntity.status(HttpStatus.OK).body(userInfoDto); // Trả về thông tin của user và mã trạng thái 200 OK
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Trả về mã trạng thái 400 Bad Request
        }
    }

    // Chuyển đổi từ User sang UserInfoDto
    private UserInfoDto convertToUserInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setFullname(user.getFullname());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setAddress(user.getAddress());
        userInfoDto.setIdentifier(user.getIdentifier());
        userInfoDto.setAvatar(user.getAvatar());
        userInfoDto.setCreatedAt(user.getCreatedAt());
        userInfoDto.setUpdatedAt(user.getUpdatedAt());
        userInfoDto.setEnabled(user.getEnabled());
        userInfoDto.setRole(user.getRole().getName());
        return userInfoDto;
    }

    // Get the user info by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.getUserInfoById(id);
            UserInfoDto userInfoDto = convertToUserInfoDto(user);
            return ResponseEntity.status(HttpStatus.OK).body(userInfoDto);
        } catch (Exception e) {
            log.error("User not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update the user
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@Valid @ModelAttribute UpdateUserDto updateUserDto, @RequestHeader("Authorization") String token) {
        try {
            userService.updateUser(updateUserDto, token);
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        } catch (Exception e) {
            log.error("User update failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/register-tracking")
    public ResponseEntity<String> registerTracking(@RequestParam String email, @RequestHeader("Authorization") String token) {
        try {
            userService.registerTracking(email, token);
            return ResponseEntity.status(HttpStatus.OK).body("Tracking registration successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/by-document-count")
    public ResponseEntity<List<UserDocumentStatsDTO>> getUsersByDocumentCount() {
        List<UserDocumentStatsDTO> users = userService.getUsersOrderByDocumentCountDesc();
        return ResponseEntity.ok(users);
    }

//    @GetMapping("/by-total-views/desc")
//    public ResponseEntity<List<UserDocumentStatsDTO>> getUsersByTotalViewsDesc() {
//        List<UserDocumentStatsDTO> users = userService.getUsersOrderByTotalViewsDesc();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/by-total-views/asc")
//    public ResponseEntity<List<UserDocumentStatsDTO>> getUsersByTotalViewsAsc() {
//        List<UserDocumentStatsDTO> users = userService.getUsersOrderByTotalViewsAsc();
//        return ResponseEntity.ok(users);
//    }

}