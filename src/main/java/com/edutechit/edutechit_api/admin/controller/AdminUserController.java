package com.edutechit.edutechit_api.admin.controller;

import com.edutechit.edutechit_api.admin.service.AdminUserService;
import com.edutechit.edutechit_api.dto.ListUserDto;
import com.edutechit.edutechit_api.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // API để admin cập nhật trạng thái enabled cho user
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        adminUserService.updateUserStatus(id, enabled);
        return ResponseEntity.ok("User status updated successfully.");
    }

    @GetMapping()
    public ResponseEntity<List<ListUserDto>> getAllUsers() {
        List<ListUserDto> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListUserDto> getUserById(@PathVariable Long id) {
        Optional<ListUserDto> user = adminUserService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
