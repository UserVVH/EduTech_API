package com.edutechit.edutechit_api.configuration.jwt;

import com.edutechit.edutechit_api.configuration.security.CustomUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET;
    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION;

    public String generateToken(CustomUserDetails customUserDetails, String roleName) {
        Date now = new Date(); // Thời gian hiện tại
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION); // Thời gian hết hạn của token
        return Jwts.builder()
                .setSubject(Long.toString(customUserDetails.getUser().getId())) // Thêm subject vào token
                .claim("email", customUserDetails.getUser().getEmail()) // Thêm claim email vào token
                .claim("roleName", roleName) // Thêm claim roleName vào token
                .setIssuedAt(now) // Thời gian phát hành token
                .setExpiration(expiryDate) // Thời gian hết hạn của token
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET) // Mã hóa token
                .compact(); // Tạo token
    }


    public String getRoleNameFromToken(String token) {
        Claims claims = Jwts.parser() // Giải mã token
                .setSigningKey(JWT_SECRET) // Mã hóa token
                .parseClaimsJws(token) // Giải mã token
                .getBody(); // Lấy thông tin trong token
        return claims.get("roleName", String.class); // Lấy roleName từ token
    }

    //Tương tự như trên, nhưng lần này lấy id user từ token
    public int getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    //Tương tự như trên, nhưng lần này lấy email user từ token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    //Kiểm tra token có hợp lệ không
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken); // Giải mã token
            return true;
        } catch (MalformedJwtException ex) { // Token không đúng định dạng
            log.error("Token không đúng định dạng.");
        } catch (ExpiredJwtException ex) { // Token đã hết hạn
            log.error("Token đã hết hạn.");
        } catch (UnsupportedJwtException ex) { // Token không được hỗ trợ
            log.error("Token không được hỗ trợ.");
        } catch (IllegalArgumentException ex) { // Token truyền vào không hợp lệ
            log.error("Token truyền vào không hợp lệ.");
        }
        return false;
    }
}