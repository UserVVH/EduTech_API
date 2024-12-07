package com.edutechit.edutechit_api.service.auth;

import com.edutechit.edutechit_api.dto.RegisterRequestDto;
import com.edutechit.edutechit_api.dto.ChangePasswordDto;
import com.edutechit.edutechit_api.exception.AuthenticationFailedException;
import com.edutechit.edutechit_api.configuration.security.CustomUserDetails;
import com.edutechit.edutechit_api.configuration.jwt.JwtTokenProvider;
import com.edutechit.edutechit_api.entity.Role;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.repository.RoleRepository;
import com.edutechit.edutechit_api.repository.UserRepository;
import com.edutechit.edutechit_api.util.DropboxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

  @Autowired
  private Validator validator;  // Thêm Validator để xác thực DTO

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private DropboxUtils dropboxUtils;

  @Override
  public void register(RegisterRequestDto registerRequestDto) {

    // Xác thực DTO
    Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(
        registerRequestDto);
    if (!violations.isEmpty()) {
      // Nếu có lỗi xác thực, tạo thông báo lỗi và ném ngoại lệ
      StringBuilder errorMessage = new StringBuilder("Lỗi xác thực: ");
      for (ConstraintViolation<RegisterRequestDto> violation : violations) {
        errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage())
            .append(". ");
      }
      throw new RuntimeException(errorMessage.toString());
    }

    // Kiểm tra định dạng email
    if (!isValidEmail(registerRequestDto.getEmail())) {
      throw new RuntimeException("Email không hợp lệ");
    }

    // Kiểm tra email đã tồn tại chưa
    if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
      throw new RuntimeException("Email đã được sử dụng");
    }

    // Kiểm tra mật khẩu có đáp ứng điều kiện không
    if (!isValidPassword(registerRequestDto.getPassword())) {
      throw new RuntimeException(
          "Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm một chữ hoa, một chữ thường, một số và một ký tự đặc biệt");
    }

    // Kiểm tra role và identifier hợp lệ
    String role = registerRequestDto.getRole() != null ? registerRequestDto.getRole() : "USER";
    String identifier = registerRequestDto.getIdentifier();

    if ("STUDENT".equals(role) && !isValidStudentId(identifier)) {
      throw new RuntimeException(
          "Mã sinh viên không hợp lệ. Nó phải bắt đầu bằng 'SV' và theo sau là 6 chữ số.");
    } else if ("TEACHER".equals(role) && !isValidTeacherId(identifier)) {
      throw new RuntimeException(
          "Mã giáo viên không hợp lệ. Nó phải bắt đầu bằng 'GV' và theo sau là 6 chữ số.");
    } else if ("USER".equals(role) && identifier != null) {
      throw new RuntimeException("Người dùng với vai trò USER không được nhập mã định danh.");
    }

    // Kiểm tra identifier đã tồn tại chưa
    if (identifier != null && userRepository.findByIdentifier(identifier).isPresent()) {
      throw new RuntimeException("Identifier đã được sử dụng");
    }

    // Tạo mới user
    User user = new User();
    user.setEmail(registerRequestDto.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword())); // Mã hóa mật khẩu
    user.setFullname(registerRequestDto.getFullname());
    user.setIdentifier(identifier);
    user.setAddress(registerRequestDto.getAddress());

    // Xử lý avatar nếu có
    if (registerRequestDto.getAvatar() != null) {
      String avatarPath = saveAvatarToDropbox(registerRequestDto.getAvatar());
      user.setAvatar(avatarPath);
    }

    // Tìm và gán role cho user
    Role userRole = roleRepository.findByName(role)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));
    user.setRole(userRole);

    // Lưu user vào cơ sở dữ liệu
    userRepository.save(user);
  }

  // Kiểm tra định dạng email
  private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // Định dạng email chính xác hơn
    return Pattern.compile(emailRegex).matcher(email).matches();
  }

  // Kiểm tra định dạng mật khẩu
  private boolean isValidPassword(String password) {
    if (password.length() < 8) {
      return false;
    }
    boolean hasUpperCase = !password.equals(password.toLowerCase());
    boolean hasLowerCase = !password.equals(password.toUpperCase());
    boolean hasDigit = password.chars().anyMatch(Character::isDigit);
    boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()-_+=<>?/".indexOf(ch) >= 0);

    return hasUpperCase && hasLowerCase && hasDigit
        && hasSpecialChar; // Kiểm tra các điều kiện mật khẩu
  }

  // Kiểm tra định dạng mã sinh viên
  private boolean isValidStudentId(String identifier) {
    return identifier != null && Pattern.matches("SV\\d{6}",
        identifier); // Định dạng mã sinh viên: SV + 6 số
  }

  // Kiểm tra định dạng mã giáo viên
  private boolean isValidTeacherId(String identifier) {
    return identifier != null && Pattern.matches("GV\\d{6}",
        identifier); // Định dạng mã giáo viên: GV + 6 số
  }

  private String saveAvatarToDropbox(MultipartFile avatar) {
    try (InputStream in = avatar.getInputStream()) {
      String filename =
          "avatar_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"))
              + getFileExtension(avatar.getOriginalFilename());
      // Upload file to Dropbox and retrieve the shared link
      String filePath = dropboxUtils.uploadFile(in, filename);
      return dropboxUtils.getSharedLink(filePath);
    } catch (Exception e) {
      log.error("Error uploading avatar to Dropbox: {}", e.getMessage());
      throw new RuntimeException("Failed to save avatar. Please try again.", e);
    }
  }

  // Lấy phần mở rộng của file
  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex); // Lấy phần mở rộng của file
  }

  // Xử lý logic đăng nhập
  @Override
  public String login(String email, String password) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, password)
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      User user = userDetails.getUser();
      return jwtTokenProvider.generateToken(userDetails, user.getRole().getName());
    } catch (Exception e) {
      log.error("Login failed: {}", e.getMessage());
      if (e.getMessage().equals("User is disabled")) {
        throw new AuthenticationFailedException(
            "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ!");
      }
      throw new AuthenticationFailedException("Email hoặc mật khẩu không đúng. Vui lòng thử lại!");
    }
  }

  // Xử lý quên mật khẩu
  @Override
  public void forgotPassword(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    String newPassword = generateRandomPassword();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    sendResetPasswordEmail(email, newPassword);
  }

  // Tạo mật khẩu ngẫu nhiên 15 ký tự
  private String generateRandomPassword() {
    String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lower = "abcdefghijklmnopqrstuvwxyz";
    String digits = "0123456789";
    String special = "!@#$%^&*()-_+=<>?/";

    String allCharacters = upper + lower + digits + special;
    Random random = new SecureRandom();

    StringBuilder password = new StringBuilder();
    password.append(upper.charAt(random.nextInt(upper.length())));
    password.append(lower.charAt(random.nextInt(lower.length())));
    password.append(digits.charAt(random.nextInt(digits.length())));
    password.append(special.charAt(random.nextInt(special.length())));

    for (int i = 4; i < 15; i++) {
      password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
    }

    return password.toString();
  }

  // Gửi email khôi phục mật khẩu
  private void sendResetPasswordEmail(String toEmail, String newPassword) {
    final String fromEmail = "notification.eduvnua@gmail.com";
    final String password = "thzr pqrw izim cpap";

    Properties properties = new Properties();
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(fromEmail, password);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
      message.setSubject("Thông Báo Khôi Phục Mật Khẩu");

      String htmlContent = "<!DOCTYPE html>" +
          "<html>" +
          "<head>" +
          "<style>" +
          "body {font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0;}"
          +
          ".container {max-width: 600px; margin: 30px auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);}"
          +
          ".header {background-color: #007BFF; padding: 15px; text-align: center; color: #ffffff; font-size: 24px; font-weight: bold; border-top-left-radius: 10px; border-top-right-radius: 10px;}"
          +
          ".content {padding: 20px; line-height: 1.8; color: #555555;}" +
          ".content p {margin: 0 0 15px;}" +
          ".button {display: inline-block; padding: 12px 25px; color: #ffffff;  border-radius: 5px; text-decoration: none; font-weight: bold; text-align: center;}"
          +
          ".password {font-weight: bold; font-size: 18px; color: #d32f2f; padding: 5px 10px; background-color: #fbe9e7; display: inline-block; border-radius: 5px; margin-top: 10px;}"
          +
          ".warning {font-size: 16px; font-weight: bold; color: #e65100; background-color: #fff3e0; padding: 10px; border-radius: 5px; margin-top: 15px;}"
          +
          ".footer {margin-top: 20px; font-size: 14px; color: #777777; text-align: center; border-top: 1px solid #dddddd; padding-top: 15px;}"
          +
          "</style>" +
          "</head>" +
          "<body>" +
          "<div class='container'>" +
          "<div class='header'>Thông Báo Khôi Phục Mật Khẩu</div>" +
          "<div class='content'>" +
          "<p>Xin chào,</p>" +
          "<p>Mật khẩu của bạn đã được đặt lại thành công. Mật khẩu mới của bạn là:</p>" +
          "<p class='password'>" + newPassword + "</p>" +
          "<p>Hãy sao chép mật khẩu này và giữ bí mật.</p>" +
          "<a href='http://localhost:3000/' class='button'>Đăng Nhập Ngay</a>" +
          "<p class='warning'>Vui lòng thay đổi mật khẩu ngay sau khi đăng nhập thành công!</p>" +
          "</div>" +
          "<div class='footer'>" +
          "<p>Nếu bạn không yêu cầu thay đổi mật khẩu này, vui lòng liên hệ bộ phận hỗ trợ của chúng tôi.</p>"
          +
          "</div>" +
          "</div>" +
          "</body>" +
          "</html>";

      message.setContent(htmlContent, "text/html; charset=utf-8");

      Transport.send(message);
      log.info("Mật khẩu mới đã được gửi đến email: {}", toEmail);
    } catch (MessagingException e) {
      log.error("Lỗi gửi mật khẩu mới.", e);
    }
  }

  // Xử lý thay đổi mật khẩu
  @Override
  public void changePassword(ChangePasswordDto changePasswordDto, String token) {
    String email = jwtTokenProvider.getEmailFromToken(token);

    if (email == null) {
      throw new RuntimeException("Token không hợp lệ");
    }

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

    if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
      throw new RuntimeException("Mật khẩu cũ không chính xác.");
    }

    if (passwordEncoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
      throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ.");
    }

    if (!isValidPassword(changePasswordDto.getNewPassword())) {
      throw new RuntimeException("Mật khẩu mới không đáp ứng yêu cầu.");
    }

    if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getReNewPassword())) {
      throw new RuntimeException("Mật khẩu nhập lại không khớp với mật khẩu mới.");
    }

    user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
    userRepository.save(user);
  }
}
