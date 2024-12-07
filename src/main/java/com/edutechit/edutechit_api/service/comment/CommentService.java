package com.edutechit.edutechit_api.service.comment;

import com.edutechit.edutechit_api.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long documentId, String content, String token);
    CommentDto updateComment(Long commentId, String content, String token);
    void deleteComment(Long commentId, String token);
    List<CommentDto> getComments(Long documentId);
}
