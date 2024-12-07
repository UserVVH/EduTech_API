package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.dto.ListUserDto;

import java.util.List;
import java.util.Optional;

public interface AdminUserService {
    void updateUserStatus(Long userId, Boolean enabled);
    List<ListUserDto> getAllUsers();
    Optional<ListUserDto> getUserById(Long id);
}
