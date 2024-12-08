package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.configuration.jwt.JwtTokenProvider;
import com.edutechit.edutechit_api.dto.CommentDto;
import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.entity.Category;
import com.edutechit.edutechit_api.entity.Comment;
import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.entity.File;
import com.edutechit.edutechit_api.entity.Follow;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.exception.CategoryNotFoundException;
import com.edutechit.edutechit_api.exception.DocumentNotFoundException;
import com.edutechit.edutechit_api.exception.ResourceNotFoundException;
import com.edutechit.edutechit_api.exception.UserNotFoundException;
import com.edutechit.edutechit_api.repository.CategoryRepository;
import com.edutechit.edutechit_api.repository.CommentRepository;
import com.edutechit.edutechit_api.repository.DocumentRepository;
import com.edutechit.edutechit_api.repository.FileRepository;
import com.edutechit.edutechit_api.repository.FollowRepository;
import com.edutechit.edutechit_api.repository.UserRepository;
import com.edutechit.edutechit_api.service.email.EmailService;
import com.edutechit.edutechit_api.util.DropboxUtils;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminDocumentServiceImpl implements AdminDocumentService {

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private DropboxUtils dropboxUtils;


  //Duyệt hoặc từ chối tài liệu
  @Override
  public void updateDocumentStatus(Long documentId, Document.Status status) {
    Document document = documentRepository.findById(documentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Document not found with id " + documentId));

    document.setStatus(status);
    document.setUpdatedAt(LocalDateTime.now());
    documentRepository.save(document);

    if (status == Document.Status.VERIFIED) {
      List<Follow> followers = followRepository.findAll();
      String[] emailAddresses = followers.stream()
          .map(Follow::getEmail)
          .toArray(String[]::new);

      emailService.sendEmail(emailAddresses, "Tài Liệu Mới Đã Được Xác Thực",
          "Một tài liệu mới đã được xác thực, bạn có thể truy cập đến: http://localhost:3000/ để xem ngay.");
    }
  }

  @Override
  @Transactional
  public DocumentDto createDocument(DocumentCreateDto documentCreateDTO, String token) {
    try {
      Document document = new Document();
      document.setTitle(documentCreateDTO.getTitle());
      document.setDescription(documentCreateDTO.getDescription());
      document.setAuthor(documentCreateDTO.getAuthor());
      document.setPublisher(documentCreateDTO.getPublisher());
      document.setPublishingYear(documentCreateDTO.getPublishingYear());
      document.setCreatedAt(LocalDateTime.now());
      document.setUpdatedAt(LocalDateTime.now());
      document.setStatus(Document.Status.VERIFIED);

      long userId = getUserIdFromToken(token);
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new UserNotFoundException("User not found"));
      document.setUser(user);

      Category category = categoryRepository.findById(documentCreateDTO.getCategoryId())
          .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
      document.setCategory(category);

      if (documentCreateDTO.getImage() != null) {
        String imageLink = saveImageToDropbox(documentCreateDTO.getImage());
        saveImageLinkToDatabase(imageLink, document);
      }

      DocumentDto documentDto = toDTO(document);
      if (documentCreateDTO.getPdfFiles() != null) {
        String pdfFileName = savePdfFileToDropbox(documentCreateDTO.getPdfFiles(), document);
        documentDto.setPdfFiles(pdfFileName);
      }
      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create document", e);
    }
  }

  private String savePdfFileToDropbox(MultipartFile pdfFile, Document document) {
    if (pdfFile == null) {
      throw new IllegalArgumentException("PDF file must not be null");
    }
    try (InputStream in = pdfFile.getInputStream()) {
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
      String extension = getFileExtension(pdfFile.getOriginalFilename());
      String filename = "pdf_" + timestamp + extension;

      String filePath = dropboxUtils.uploadFile(in, filename);
      String fileLink = dropboxUtils.getSharedLink(filePath);
      saveFileInfo(fileLink, document);
      return fileLink;
    } catch (Exception e) {
      throw new RuntimeException("Failed to save PDF to Dropbox", e);
    }
  }

  private void saveFileInfo(String url, Document document) {
    File existingFile = fileRepository.findByDocument(document);
    if (existingFile == null) {
      File file = new File();
      file.setFilename(url.substring(url.lastIndexOf('/') + 1));
      file.setFilePath(url);
      file.setCreatedAt(LocalDateTime.now());
      file.setDocument(document);
      fileRepository.save(file);
    } else {
      existingFile.setFilePath(url);
      existingFile.setFilename(url.substring(url.lastIndexOf('/') + 1));
      fileRepository.save(existingFile);
    }
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex);
  }

  private Long getUserIdFromToken(String token) {
    String email = jwtTokenProvider.getEmailFromToken(token);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
    return user.getId();
  }

  private DocumentDto toDTO(Document document) {
    DocumentDto documentDto = new DocumentDto();
    documentDto.setId(document.getId());
    documentDto.setTitle(document.getTitle());
    documentDto.setDescription(document.getDescription());
    documentDto.setAuthor(document.getAuthor());
    documentDto.setPublisher(document.getPublisher());
    documentDto.setPublishingYear(document.getPublishingYear());
    documentDto.setImage(document.getImage());
    documentDto.setCategoryName(document.getCategory().getName());
    documentDto.setUserId(document.getUser().getId());
    documentDto.setUserName(document.getUser().getFullname());
    documentDto.setCreatedAt(document.getCreatedAt());
    documentDto.setUpdatedAt(document.getUpdatedAt());
    if (document.getFile() != null) {
      documentDto.setPdfFiles(document.getFile().getFilePath());
    }
    documentDto.setView(document.getView());
    documentDto.setStatus(document.getStatus());
    return documentDto;
  }

  private String saveImageToDropbox(MultipartFile imageFile) {
    try (InputStream in = imageFile.getInputStream()) {
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
      String extension = getFileExtension(imageFile.getOriginalFilename());
      String filename = "image_" + timestamp + extension;

      String filePath = dropboxUtils.uploadFile(in, filename);
      return dropboxUtils.getSharedLink(filePath);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save image to Dropbox", e);
    }
  }

  private void saveImageLinkToDatabase(String imageLink, Document document) {
    document.setImage(imageLink);
    documentRepository.save(document);
  }

  @Override
  @Transactional
  public DocumentDto updateDocument(Long id, DocumentCreateDto documentCreateDTO, String token) {
    try {
      Document document = documentRepository.findById(id)
          .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
      document.setTitle(documentCreateDTO.getTitle());
      document.setDescription(documentCreateDTO.getDescription());
      document.setAuthor(documentCreateDTO.getAuthor());
      document.setPublisher(documentCreateDTO.getPublisher());
      document.setPublishingYear(documentCreateDTO.getPublishingYear());
      document.setUpdatedAt(LocalDateTime.now());

      Category category = categoryRepository.findById(documentCreateDTO.getCategoryId())
          .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
      document.setCategory(category);

      document = documentRepository.save(document);

      if (documentCreateDTO.getImage() != null) {
        String imageLink = saveImageToDropbox(documentCreateDTO.getImage());
        saveImageLinkToDatabase(imageLink, document);
      }

      DocumentDto documentDto = toDTO(document);
      if (documentCreateDTO.getPdfFiles() != null) {
        String pdfFileName = savePdfFileToDropbox(documentCreateDTO.getPdfFiles(), document);
        documentDto.setPdfFiles(pdfFileName);
      }
      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to update document", e);
    }
  }

  @Override
  @Transactional
  public DocumentDto getDocument(Long id) {
    try {
      Document document = documentRepository.findById(id)
          .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
      document.setView(document.getView() + 1);
      document = documentRepository.save(document);

      DocumentDto documentDto = toDTO(document);

      List<Comment> comments = commentRepository.findCommentsByDocumentId(id);
      Set<CommentDto> commentDtos = comments.stream()
          .map(this::toCommentDto)
          .collect(Collectors.toSet());

      documentDto.setComments(commentDtos);
      documentDto.setCategoryName(document.getCategory().getName());
      documentDto.setPdfFiles(
          documentRepository.findFilePathsByDocumentId(document.getId()).get(0));

      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to get document", e);
    }
  }

  private CommentDto toCommentDto(Comment comment) {
    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setContent(comment.getContent());
    commentDto.setUserId(comment.getUser().getId());
    commentDto.setUserName(comment.getUser().getFullname());
    commentDto.setAvatar(comment.getUser().getAvatar());
    commentDto.setDocumentId(comment.getDocument().getId());
    commentDto.setCreatedAt(comment.getCreatedAt());
    commentDto.setUpdatedAt(comment.getUpdatedAt());

    return commentDto;
  }


  //Lấy tất cả tài liệu
  @Override
  public List<DocumentResponseDto> getAllDocuments() {
    List<Document> documents = documentRepository.getAll();
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Transactional
  public void deleteDocument(Long documentId) {
    Document document = documentRepository.findById(documentId)
        .orElseThrow(
            () -> new DocumentNotFoundException("Document not found with id: " + documentId));

    // First delete all comments related to the document
    fileRepository.deleteByDocument(document);
    commentRepository.deleteByDocumentId(documentId);

    // Now delete the document
    documentRepository.delete(document);
  }

  private DocumentResponseDto toResponseDTO(Document document) {
    DocumentResponseDto documentResponseDto = new DocumentResponseDto();
    documentResponseDto.setId(document.getId());
    documentResponseDto.setTitle(document.getTitle());
    documentResponseDto.setAuthor(document.getAuthor());
    documentResponseDto.setPublisher(document.getPublisher());
    documentResponseDto.setPublishingYear(document.getPublishingYear());
    documentResponseDto.setImage(document.getImage());
    documentResponseDto.setCategoryName(document.getCategory().getName());
    documentResponseDto.setUserName(document.getUser().getFullname());
    documentResponseDto.setCreatedAt(document.getCreatedAt());
    documentResponseDto.setUpdatedAt(document.getUpdatedAt());
    documentResponseDto.setView(document.getView());
    documentResponseDto.setStatus(document.getStatus());
    return documentResponseDto;
  }

  @Override
  public List<DocumentResponseDto> getDocumentsByCategoryName(String categoryName) {
    List<Document> documents = documentRepository.findAllDocumentsByCategoryName(categoryName);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy tất cả bài viết "CHƯA ĐƯỢC DUYỆT" của một user
  @Override
  public List<DocumentResponseDto> getDocumentsByUserIdAndStatusCreated(Long userId) {
    List<Document> documents = documentRepository.findDocumentsByUserIdAndCREATED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy tất cả bài viết "ĐÃ ĐƯỢC DUYỆT" của một user
  @Override
  public List<DocumentResponseDto> getDocumentsByUserIdAndStatusVerified(Long userId) {
    List<Document> documents = documentRepository.findDocumentsByUserIdAndVERIFIED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy tất cả bài viết "BỊ TỪ CHỐI" của một user
  @Override
  public List<DocumentResponseDto> getDocumentsByUserIdAndStatusRejected(Long userId) {
    List<Document> documents = documentRepository.findDocumentsByUserIdAndREJECTED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy tất cả bài viết của một user (bất kể trạng thái)
  @Override
  public List<DocumentResponseDto> getAllDocumentsByUserId(Long userId) {
    List<Document> documents = documentRepository.findAllDocumentsByUserId(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy danh sách tất cả các tài liệu "ĐÃ ĐƯỢC DUYỆT"
  @Override
  public List<DocumentResponseDto> getAllDocumentsByStatusVerified() {
    List<Document> documents = documentRepository.findAllDocumentsByStatusVerified();
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy danh sách tất cả các tài liệu "CHƯA ĐƯỢC DUYỆT"
  @Override
  public List<DocumentResponseDto> getAllDocumentsByStatusCreated() {
    List<Document> documents = documentRepository.findAllDocumentsByStatusCreated();
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  // Lấy danh sách tất cả các tài liệu "BỊ TỪ CHỐI"
  @Override
  public List<DocumentResponseDto> getAllDocumentsByStatusRejected() {
    List<Document> documents = documentRepository.findAllDocumentsByStatusRejected();
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Override
  public List<DocumentResponseDto> getDocumentsSorted(String sortBy, String order) {
    List<Document> documents = documentRepository.findAllSorted(sortBy, order);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Override
  public List<DocumentResponseDto> searchDocumentsByTitle(String title) {
    List<Document> documents = documentRepository.findByTitleContainingIgnoreCase(title);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

}