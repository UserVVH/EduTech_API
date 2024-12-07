package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.dto.ListUserDto;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.exception.ResourceNotFoundException;
import com.edutechit.edutechit_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserServiceImpl implements AdminUserService{
    private final UserRepository userRepository;

    @Autowired
    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateUserStatus(Long userId, Boolean enabled) {
        // Tìm kiếm user theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Cập nhật trạng thái
        user.setEnabled(enabled);

        // Lưu lại thay đổi
        userRepository.save(user);
    }
    @Override
    public List<ListUserDto> getAllUsers() {
        return userRepository.findAllUsersAsListUserDto();
    }

    @Override
    public Optional<ListUserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new ListUserDto(user.getId(), user.getFullname(), user.getEmail(), user.getAddress(),
                        user.getIdentifier(), user.getAvatar(), user.getCreatedAt(),
                        user.getUpdatedAt(), user.getEnabled(), user.getRole().getName()));
    }
}
