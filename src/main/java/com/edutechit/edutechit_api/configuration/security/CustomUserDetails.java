package com.edutechit.edutechit_api.configuration.security;

import com.edutechit.edutechit_api.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public static CustomUserDetails create(User user) {
        return new CustomUserDetails(user);
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    // Trả về danh sách quyền của người dùng dưới dạng Collection<GrantedAuthority>
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về quyền hạn mà không có tiền tố "ROLE_"
        return List.of(new SimpleGrantedAuthority(user.getRole().getName())); // Chỉ trả về tên vai trò
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Sử dụng email làm tên người dùng
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Thông tin xác thực không hết hạn
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled(); // Kiểm tra xem tài khoản có được kích hoạt không
    }
}
