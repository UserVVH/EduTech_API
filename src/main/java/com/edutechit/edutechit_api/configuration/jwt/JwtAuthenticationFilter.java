package com.edutechit.edutechit_api.configuration.jwt;

import com.edutechit.edutechit_api.configuration.security.CustomUserDetails;
import com.edutechit.edutechit_api.configuration.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // Lấy token từ request
    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader)) {
            return authHeader; // Trả về trực tiếp token mà không cần loại bỏ "Bearer "
        }
        return null;
    }

    // Xác thực token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) { // Kiểm tra xem token có hợp lệ không
                long userId = jwtTokenProvider.getUserIdFromJwt(jwt); // Lấy userId từ token
                CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(userId); // Lấy thông tin user từ userId
                if (customUserDetails != null) {
                    String roleName = jwtTokenProvider.getRoleNameFromToken(jwt); // Lấy roleName từ token
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName); // Tạo một authority từ roleName
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( // Tạo một authentication token
                            customUserDetails,
                            null,
                            List.of(authority)
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set thông tin chi tiết của authentication token
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Set authentication token vào context
                }
            }
        } catch (Exception ex) {
            log.error("Failed to set user authentication", ex);
        }
        filterChain.doFilter(request, response);
    }
}