package com.edutechit.edutechit_api.service.comment;

import com.edutechit.edutechit_api.configuration.jwt.JwtTokenProvider;
import com.edutechit.edutechit_api.dto.CommentDto;
import com.edutechit.edutechit_api.entity.Comment;
import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.entity.User;
import com.edutechit.edutechit_api.exception.CommentNotFoundException;
import com.edutechit.edutechit_api.exception.DocumentNotFoundException;
import com.edutechit.edutechit_api.exception.UserNotFoundException;
import com.edutechit.edutechit_api.repository.CommentRepository;
import com.edutechit.edutechit_api.repository.DocumentRepository;
import com.edutechit.edutechit_api.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Override
  @Transactional
  public CommentDto addComment(Long documentId, String content, String token) {
    Document document = documentRepository.findById(documentId)
        .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

    long userId = jwtTokenProvider.getUserIdFromJwt(token.replace("Bearer ", ""));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));

    Comment comment = new Comment();
    comment.setContent(content);
    comment.setUser(user);
    comment.setDocument(document);
    comment.setCreatedAt(LocalDateTime.now());
    comment.setUpdatedAt(LocalDateTime.now());

    commentRepository.save(comment);

    return toDto(comment);
  }

  @Override
  @Transactional
  public CommentDto updateComment(Long commentId, String content, String token) {
    long userId = jwtTokenProvider.getUserIdFromJwt(token.replace("Bearer ", ""));
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

    if (comment.getUser().getId() != userId) {
      throw new RuntimeException("You do not have permission to edit this comment");
    }

    comment.setContent(content);
    comment.setUpdatedAt(LocalDateTime.now());
    commentRepository.save(comment);

    return toDto(comment);
  }

  @Override
  @Transactional
  public void deleteComment(Long commentId, String token) {
    long userId = jwtTokenProvider.getUserIdFromJwt(token.replace("Bearer ", ""));
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

    //chỉ người viết bình luận hoặc chủ sở hữu tài liệu chứa bình luận mới có quyền xóa bình luận.
    if (comment.getUser().getId() != userId && !comment.getDocument().getUser().getId()
        .equals(userId)) {
      throw new RuntimeException("You do not have permission to delete this comment");
    }

    commentRepository.delete(comment);
  }

  //lấy các bình luận của 1 tài liệu
  @Override
  public List<CommentDto> getComments(Long documentId) {
    return commentRepository.findByDocumentId(documentId)
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  private CommentDto toDto(Comment comment) {
    CommentDto dto = new CommentDto();
    dto.setId(comment.getId());
    dto.setContent(comment.getContent());
    dto.setUserId(comment.getUser().getId());
    dto.setUserName(comment.getUser().getFullname());
    dto.setAvatar(comment.getUser().getAvatar());
    dto.setDocumentId(comment.getDocument().getId());
    dto.setCreatedAt(comment.getCreatedAt());
    dto.setUpdatedAt(comment.getUpdatedAt());
    return dto;
  }
}
