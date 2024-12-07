package com.edutechit.edutechit_api.service.user;


import com.edutechit.edutechit_api.dto.UpdateUserDto;
import com.edutechit.edutechit_api.dto.UserDocumentStatsDTO;
import com.edutechit.edutechit_api.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAll();
    void updateUser(UpdateUserDto updateUserDto, String token);
    User getUserInfoByToken(String token);
    User getUserInfoById(Long id);
    List<User> searchUsersByName(String fullname);
    void registerTracking(String email, String token);
    List<UserDocumentStatsDTO> getUsersOrderByDocumentCountDesc();
}

