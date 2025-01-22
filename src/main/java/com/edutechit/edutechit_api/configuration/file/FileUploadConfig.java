package com.edutechit.edutechit_api.configuration.file;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxFileSize;

  @Value("${spring.servlet.multipart.max-request-size}")
  private String maxRequestSize;

  // Set the maximum file size to 100MB
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.parse(maxFileSize));
    factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
    return factory.createMultipartConfig();
  }
}