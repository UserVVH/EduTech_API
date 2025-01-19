package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.CommentDto;
import com.edutechit.edutechit_api.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Thêm bình luận vào tài liệu
    @PostMapping("/{documentId}/add")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long documentId, @RequestBody String content, @RequestHeader("Authorization") String token) {
        CommentDto commentDto = commentService.addComment(documentId, content.trim(), token);
        return ResponseEntity.ok(commentDto);
    }

    // Sửa bình luận
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId, @RequestBody String content, @RequestHeader("Authorization") String token) {
        CommentDto commentDto = commentService.updateComment(commentId, content.trim(), token);
        return ResponseEntity.ok(commentDto);
    }

    // Xóa bình luận
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token) {
        commentService.deleteComment(commentId, token);
        return ResponseEntity.noContent().build();
    }

    // Lấy danh sách bình luận của 1 tài liệu
    @GetMapping("/{documentId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long documentId) {
        return ResponseEntity.ok(commentService.getComments(documentId));
    }
}
