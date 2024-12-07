package com.edutechit.edutechit_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Xử lý ngoại lệ xác thực thất bại
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<String> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        log.error("Xác thực thất bại: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // Xử lý ngoại lệ không tìm thấy tài nguyên (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NoHandlerFoundException ex) {
        log.error("Không tìm thấy tài nguyên: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài nguyên: " + ex.getRequestURL());
    }

    // Xử lý ngoại lệ khi dữ liệu không hợp lệ (Validation Errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Xử lý ngoại lệ khi tài liệu không được tìm thấy
    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        log.error("Document not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Xử lý ngoại lệ khi người dùng không được tìm thấy
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Xử lý ngoại lệ khi bình luận không được tìm thấy
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.error("Comment not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Xử lý ngoại lệ chung cho các lỗi không xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Lỗi hệ thống: ", ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Đã xảy ra lỗi hệ thống: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
