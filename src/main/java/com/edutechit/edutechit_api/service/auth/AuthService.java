package com.edutechit.edutechit_api.service.auth;

import com.edutechit.edutechit_api.dto.RegisterRequestDto;
import com.edutechit.edutechit_api.dto.ChangePasswordDto;

public interface AuthService {
    String login(String email, String password);
    void register(RegisterRequestDto registerRequestDto);
    void forgotPassword(String email);
    void changePassword(ChangePasswordDto changePasswordDto, String token);
}