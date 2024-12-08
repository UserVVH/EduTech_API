package com.edutechit.edutechit_api.admin.controller;

import com.edutechit.edutechit_api.admin.service.AdminUserService;
import com.edutechit.edutechit_api.dto.ListUserDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // API để admin cập nhật trạng thái khóa/mở khóa cho user
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
