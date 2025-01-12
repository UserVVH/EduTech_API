package com.edutechit.edutechit_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.ToString;

@Data
@Entity
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Lob
  @Column(columnDefinition = "TEXT")  // da thay cai moi
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private String author;

  @Column(nullable = false)
  private String image;

  private String publisher;

  private String publishingYear;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  public enum Status {
    CREATED,
    VERIFIED,
    REJECTED
  }

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

//  optional = false là một tài liệu phải có 1  file
//  @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
  @OneToOne(mappedBy = "document", fetch = FetchType.LAZY, optional = false)
  @ToString.Exclude
  private File file;

//  @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OneToMany(mappedBy = "document", orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<Comment> comments = new HashSet<>();

  @Column(nullable = false)
  private int view = 0;

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}