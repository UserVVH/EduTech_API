package com.edutechit.edutechit_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String filename;

  @Column(nullable = false, length = 255)
  private String filePath;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  //    @OneToOne(fetch = FetchType.LAZY)
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "document_id", nullable = false)
  @JsonIgnore
  private Document document;
}