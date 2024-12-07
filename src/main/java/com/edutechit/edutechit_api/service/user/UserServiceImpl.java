package com.edutechit.edutechit_api.service.user;

import com.edutechit.edutechit_api.configuration.jwt.JwtTokenProvider;
import com.edutechit.edutechit_api.dto.UpdateUserDto;
import com.edutechit.edutechit_api.dto.UserDocumentStatsDTO;
import com.edutechit.edutechit_api.entity.Follow;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.repository.FollowRepository;
import com.edutechit.edutechit_api.repository.UserRepository;
import com.edutechit.edutechit_api.util.DropboxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private DropboxUtils dropboxUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(UpdateUserDto updateUserDto, String token) {
        long userId = jwtTokenProvider.getUserIdFromJwt(token.replace("Bearer ", ""));
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFullname(updateUserDto.getFullname());
            if (updateUserDto.getAvatar() != null) {
                String avatarLink = saveAvatarFileToDropbox(updateUserDto.getAvatar());
                user.setAvatar(avatarLink);
            }
            user.setIdentifier(updateUserDto.getIdentifier());
            user.setAddress(updateUserDto.getAddress());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private String saveAvatarFileToDropbox(MultipartFile avatarFile) {
        try (InputStream in = avatarFile.getInputStream()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
            String filename = "avatar_" + timestamp + getFileExtension(avatarFile.getOriginalFilename());

            // Upload to Dropbox and get the shared link
            String filePath = dropboxUtils.uploadFile(in, filename);
            return dropboxUtils.getSharedLink(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save avatar file to Dropbox", e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }

    @Override
    public User getUserInfoByToken(String token) {
        long userId = jwtTokenProvider.getUserIdFromJwt(token.replace("Bearer ", ""));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserInfoById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> searchUsersByName(String fullname) {
        return userRepository.findByFullnameContainingIgnoreCase(fullname);
    }

    @Override
    public void registerTracking(String email, String token) {
        String tokenEmail = jwtTokenProvider.getEmailFromToken(token.replace("Bearer ", ""));
        if (!email.equals(tokenEmail)) {
            throw new RuntimeException("Email does not match the token's email");
        }

        if (followRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already being followed");
        }

        Follow follow = new Follow();
        follow.setEmail(email);
        followRepository.save(follow);
    }

    @Override
    public List<UserDocumentStatsDTO> getUsersOrderByDocumentCountDesc() {
        return userRepository.findUsersOrderByDocumentCountDesc();
    }
}